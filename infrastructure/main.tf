variable "db_password" {
  description = "The password for the RDS database"
  type        = string
}

provider "aws" {
  region = "us-east-2"
}

# Security group for the k3s node (opens SSH, HTTP, and NodePort for UI)
resource "aws_security_group" "k3s_sg" {
  name        = "k3s_sg"
  description = "Allow inbound traffic for K3s and NodePort UI"

  # Allow K3s API access for Lens GUI
  ingress {
    from_port   = 6443
    to_port     = 6443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }


  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # NodePort exposed for UI Service inside K8s
  ingress {
    from_port   = 30080
    to_port     = 30080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# EC2 instance that will run k3s. user_data installs k3s on boot.
resource "aws_instance" "k3s_node" {
  ami                    = "ami-024e6efaf93d85776"
  instance_type          = "t3.micro"
  key_name               = "shareu-key"
  vpc_security_group_ids = [aws_security_group.k3s_sg.id]

  user_data = <<-EOF
              #!/bin/bash
              set -e
              curl -sfL https://get.k3s.io | sh -
              mkdir -p /home/ubuntu/.kube
              # Wait for K3s to create config, then copy it for ubuntu user
              sleep 30
              if [ -f /etc/rancher/k3s/k3s.yaml ]; then
                cp /etc/rancher/k3s/k3s.yaml /home/ubuntu/.kube/config
                chown ubuntu:ubuntu /home/ubuntu/.kube/config
              fi
              EOF

  tags = {
    Name = "ShareU-K3s-Node"
  }
}

# Static Elastic IP for the k3s node. Use this IP with NodePort 30080.
resource "aws_eip" "k3s_node_eip" {
  domain   = "vpc"
  instance = aws_instance.k3s_node.id

  tags = {
    Name = "ShareU-K3s-Elastic-IP"
  }
}

# RDS security group allowing MySQL access from the k3s node SG
resource "aws_security_group" "rds_sg" {
  name        = "rds_sg"
  description = "Allow MySQL from k3s nodes"

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.k3s_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Managed RDS instance for ShareU
resource "aws_db_instance" "shareu_db" {
  allocated_storage      = 20
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = "db.t3.micro"
  db_name                = "shareudb"
  username               = "admin"
  password               = var.db_password
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  skip_final_snapshot    = true
  publicly_accessible    = true
}

# ==========================================
# S3 Bucket & IAM User Setup for shareU
# ==========================================

# S3 bucket for media (bucket names must be globally unique)
resource "aws_s3_bucket" "media_bucket" {
  bucket = "shareu-comp4442-media-bucket-12345" # change 12345 to your student id or random suffix
}

# Allow public read access for objects (optional)
resource "aws_s3_bucket_public_access_block" "media_bucket_public_access" {
  bucket = aws_s3_bucket.media_bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

# Bucket policy to allow public GetObject
resource "aws_s3_bucket_policy" "media_bucket_policy" {
  bucket     = aws_s3_bucket.media_bucket.id
  depends_on = [aws_s3_bucket_public_access_block.media_bucket_public_access]

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "PublicReadGetObject"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.media_bucket.arn}/*"
      }
    ]
  })
}

# IAM user for application access to S3
resource "aws_iam_user" "shareu_app_user" {
  name = "shareu-app-s3-user"
}

resource "aws_iam_user_policy_attachment" "s3_full_access" {
  user       = aws_iam_user.shareu_app_user.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}

resource "aws_iam_access_key" "shareu_app_key" {
  user = aws_iam_user.shareu_app_user.name
}

output "s3_bucket_name" {
  description = "The name of the S3 Bucket"
  value       = aws_s3_bucket.media_bucket.id
}

output "aws_access_key_id" {
  description = "AWS Access Key for Spring Boot"
  value       = aws_iam_access_key.shareu_app_key.id
}

output "aws_secret_access_key" {
  description = "AWS Secret Key for Spring Boot"
  value       = aws_iam_access_key.shareu_app_key.secret
  sensitive   = true
}

# Useful outputs: RDS endpoint and EC2 public IP
output "rds_endpoint" {
  description = "RDS endpoint for database connection"
  value       = aws_db_instance.shareu_db.endpoint
}

output "rds_address" {
  description = "RDS hostname without port for Kubernetes SPRING_DATASOURCE_URL"
  value       = aws_db_instance.shareu_db.address
}

output "ec2_public_ip" {
  description = "Public IP of the k3s EC2 node"
  value       = aws_eip.k3s_node_eip.public_ip
}

output "shareu_public_url" {
  description = "Public URL for the ShareU UI NodePort"
  value       = "http://${aws_eip.k3s_node_eip.public_ip}:30080"
}
