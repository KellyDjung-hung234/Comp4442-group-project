variable "db_password" {
  description = "The password for the RDS database"
  type        = string
}

provider "aws" {
  region = "us-east-2"
}

resource "aws_instance" "k3s_node" {
  ami           = "ami-024e6efaf93d85776" # 呢個係 us-east-2 嘅 Ubuntu 22.04 AMI
  instance_type = "t3.micro"
  key_name      = "shareu-key" 
  
  tags = {
    Name = "ShareU-K3s-Node"
  }

  vpc_security_group_ids = [aws_security_group.k3s_sg.id]
}

resource "aws_db_instance" "shareu_db" {
  allocated_storage    = 20
  engine               = "mysql"
  engine_version       = "8.0"
  instance_class       = "db.t3.micro"
  db_name              = "shareudb"
  username             = "admin"
  password             = var.db_password
  skip_final_snapshot  = true
}

resource "aws_security_group" "k3s_sg" {
  name        = "k3s_sg"
  description = "Allow inbound traffic for K3s"

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
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}