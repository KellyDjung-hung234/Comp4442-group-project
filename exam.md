下面係按 **出題內容分類** 分析 4 份 EIE4121 exam paper，包含：
**邊份 paper / 問題 / model answer / 中文解釋 / English keyword**。
4 份卷分別係：2021/22、2022/23、2023/24、2024/25 EIE4121 Machine Learning in Cyber-Security exam papers。   

---

# 1. Feature Normalization / Scaling 特徵正規化

## 出現位置

| Exam Paper | Question                                                                     |
| ---------- | ---------------------------------------------------------------------------- |
| 2022 paper | Q2(a): high magnitude feature 對 KNN / Decision Tree 有冇影響                     |
| 2023 paper | Q1: feature normalization 方法、邊啲 model sensitive / insensitive                |
| 2025 paper | Q4: KNN、Decision Tree、SVM、Naïve Bayes、Logistic Regression 需唔需要 normalization |

---

## 常見答案整理

### Q：Describe two methods for feature normalization

**Answer：**

1. **Min-Max Scaling**
   將 feature 壓縮去固定範圍，例如 `[0,1]`。

   [
   x' = \frac{x - x_{min}}{x_{max} - x_{min}}
   ]

2. **Standardization / Z-score normalization**
   將 feature 轉成 mean = 0，standard deviation = 1。

   [
   x' = \frac{x - \mu}{\sigma}
   ]

**中文解釋：**
如果一個 feature 係 salary，數值可能係幾萬；另一個 feature 係 age，數值可能係幾十。未 normalization 時，salary 會喺 distance / gradient 入面佔太大影響，model 可能錯誤以為 salary 一定最重要。

**English keywords：**
`Min-Max Scaling`, `Standardization`, `Z-score normalization`, `feature scaling`, `mean`, `standard deviation`

---

## Q：Which models are sensitive to feature normalization?

### KNN

**Answer：需要 normalization。**

**中文解釋：**
KNN 用 distance，例如 Euclidean distance，去判斷樣本近唔近。如果某個 feature 數值範圍好大，佢會 dominate distance，令其他 feature 影響變細。

**Example：**
Age = 20–60，但 income = 10,000–100,000。
未 normalization 時，income 幾乎決定晒 distance。

**English keywords：**
`KNN`, `distance-based model`, `Euclidean distance`, `scale-sensitive`

---

### SVM

**Answer：通常需要 normalization，尤其係 RBF kernel / distance-based kernel。**

**中文解釋：**
SVM 會根據 feature space 入面嘅距離同 margin 搵 decision boundary。如果 feature scale 差好遠，margin 會被大數值 feature 影響。

**English keywords：**
`SVM`, `RBF kernel`, `margin`, `decision boundary`, `scale-sensitive`

---

### Logistic Regression

**Answer：通常建議 normalization。**

**中文解釋：**
Logistic regression 用 gradient descent 或 optimization 去學 weight。如果 feature scale 差太遠，loss surface 會變得難 optimize，training 可能慢或者不穩定。
雖然理論上 logistic regression 可以學到唔同 scale 嘅 weight，但實際 training 同 regularization 都會受 scale 影響。

**English keywords：**
`Logistic Regression`, `gradient descent`, `optimization`, `regularization`, `weights`

---

## Q：Which models are insensitive to feature normalization?

### Decision Tree

**Answer：通常唔需要 normalization。**

**中文解釋：**
Decision Tree 用 threshold split，例如：

```text
age < 30?
income < 50000?
```

佢關心嘅係 feature 排序同 split point，而唔係 distance，所以 feature 數值大細唔會令某個 feature 自動 over-weighted。

**English keywords：**
`Decision Tree`, `threshold split`, `scale-insensitive`, `feature ordering`

---

### Random Forest

**Answer：通常唔需要 normalization。**

**中文解釋：**
Random Forest 係由多棵 decision tree 組成，所以同 decision tree 一樣，主要靠 threshold splitting，不依賴 feature distance。

**English keywords：**
`Random Forest`, `ensemble`, `decision trees`, `scale-insensitive`

---

### Naïve Bayes

**Answer：一般嚟講唔太需要 normalization，但視乎使用版本。**

**中文解釋：**
如果係 Gaussian Naïve Bayes，model 會估每個 feature 嘅 mean 同 variance。feature scale 改變後 mean / variance 都會一齊改，所以通常唔似 KNN 咁嚴重受 scale 影響。

**English keywords：**
`Naïve Bayes`, `Gaussian Naïve Bayes`, `mean`, `variance`, `probabilistic model`

---

# 2. Feature Selection 特徵選擇

## 出現位置

| Exam Paper | Question                                                                                        |
| ---------- | ----------------------------------------------------------------------------------------------- |
| 2022 paper | Q3: wrapper-based method using logistic regression；filter vs wrapper                            |
| 2023 paper | Q3: feature selection importance；filter method steps；filter vs wrapper；dimensionality reduction |
| 2024 paper | Q3: filter-based vs wrapper-based advantages / limitations                                      |

---

## Q：Why is feature selection important?

**Answer：**

Feature selection 重要因為可以：

1. 減少 irrelevant / redundant features
2. 降低 overfitting 風險
3. 提高 model generalization
4. 減少 computation cost
5. 令 model 更容易 interpret

**中文解釋：**
如果 dataset 有好多無用 feature，model 可能會學到 noise，而唔係真正 pattern。Cybersecurity 入面，例如 network traffic detection，有啲 feature 可能同 attack 無關，保留太多反而影響 performance。

**English keywords：**
`feature selection`, `irrelevant features`, `redundant features`, `overfitting`, `generalization`, `interpretability`

---

## Q：Wrapper-based feature selection using logistic regression steps

**Answer：**

1. Start with candidate feature subsets
2. Train logistic regression model using one subset
3. Evaluate performance using validation set / cross-validation
4. Try different feature combinations
5. Select the subset with best validation performance
6. Repeat until selecting required number of features, e.g. top 20 features

**中文解釋：**
Wrapper method 係「用 model performance 去揀 feature」。
例如有 100 features，要揀 20 個，佢會不斷試唔同 feature subset，然後用 logistic regression train model，再睇 validation accuracy / F1 score 邊組最好。

**English keywords：**
`Wrapper-based feature selection`, `Logistic Regression`, `feature subset`, `validation performance`, `cross-validation`, `model-dependent`

---

## Q：Filter-based feature selection steps

**Answer：**

1. Compute a statistical score for each feature
2. Rank features according to the score
3. Select top-k features
4. Train the final model using selected features

Common scoring methods：

* Correlation
* Mutual information
* Chi-square test
* ANOVA F-test

**中文解釋：**
Filter method 唔會真係 train model 去試每組 feature，而係用統計方法先評分。例如睇某個 feature 同 label 有幾大關係，分數高就保留。

**English keywords：**
`Filter method`, `correlation`, `mutual information`, `chi-square`, `ANOVA`, `model-independent`

---

## Q：Filter method vs Wrapper method

| Item               | Filter Method     | Wrapper Method    |
| ------------------ | ----------------- | ----------------- |
| Based on           | Statistical score | Model performance |
| Model-dependent?   | No                | Yes               |
| Speed              | Faster            | Slower            |
| Accuracy potential | May be lower      | May be higher     |
| Overfitting risk   | Lower             | Higher            |
| Computation cost   | Low               | High              |

**中文解釋：**
Filter method 好似「考試前睇每科成績，揀最高分嘅科」。
Wrapper method 好似「每次揀唔同組合落場比賽，邊組贏就用邊組」。
Wrapper method 通常更貼合指定 model，但 computational cost 高，而且容易 overfit validation set。

**English keywords：**
`Filter-based`, `Wrapper-based`, `model-independent`, `model-dependent`, `computational cost`, `overfitting`

---

## Q：Dimensionality reduction vs Feature selection

**Answer：**

Feature selection selects original features.
Dimensionality reduction creates new transformed features.

Example：

* Feature selection：由 30 features 揀 10 個原本 features
* PCA：將 30 features 轉成 10 個 principal components

**中文解釋：**
Feature selection 係「揀保留邊啲原本欄位」。
Dimensionality reduction 係「將原本 feature 混合轉換成新 feature」。
所以 PCA 出嚟嘅 component 未必可以直接解釋成原本某一個 feature。

**English keywords：**
`Dimensionality reduction`, `PCA`, `principal components`, `feature transformation`, `feature selection`

---

# 3. Overfitting / Underfitting / Validation

## 出現位置

| Exam Paper | Question                                                                          |
| ---------- | --------------------------------------------------------------------------------- |
| 2022 paper | Q2(b): training / validation set 如何 identify overfitting；Q4 polynomial regression |
| 2023 paper | Q2: training accuracy vs testing accuracy graph；improve testing performance       |
| 2024 paper | Q5: neural network underfitting / overfitting                                     |
| 2025 paper | Q2: underfitting methods；underfitting vs overfitting；CNN overfitting solution     |

---

## Q：Difference between underfitting and overfitting

**Answer：**

| Type         | Meaning                         | Training performance | Testing performance |
| ------------ | ------------------------------- | -------------------- | ------------------- |
| Underfitting | Model too simple                | Bad                  | Bad                 |
| Overfitting  | Model too complex, learns noise | Very good            | Bad                 |

**中文解釋：**

**Underfitting**：model 太簡單，連 training data 都學唔好。
例如用 straight line 去 fit 一個好複雜嘅 curve。

**Overfitting**：model 太複雜，training data 學得太好，連 noise 都記住，但新 data 表現差。

**English keywords：**
`underfitting`, `overfitting`, `bias`, `variance`, `generalization`, `training error`, `testing error`

---

## Q：How does train-validation split help identify overfitting?

**Answer：**

Training set is used to train the model.
Validation set is used to evaluate unseen performance during model selection.

If training accuracy keeps increasing but validation accuracy drops, the model is overfitting.

**中文解釋：**
Training set 係畀 model 學習；validation set 係扮「未見過嘅 data」。
如果 model 喺 training set 好高分，但 validation set 低分，代表佢只係背熟 training data，而唔係真正學到 general pattern。

**English keywords：**
`training set`, `validation set`, `generalization`, `overfitting detection`, `validation accuracy`

---

## Q：Polynomial Model A degree 2 vs Model B degree 10

2022 paper Q4 入面，真正 data 由 degree 5 polynomial 產生，但：

* Model A：degree 2
* Model B：degree 10

### Model A

**Answer：**
Model A is likely to underfit. Training error and testing error are both relatively high.

**中文解釋：**
真實關係有 (x^5)，但 Model A 只用到 (x^2)，model capacity 唔夠，所以 training 同 testing 都唔會好。

**English keywords：**
`degree 2 polynomial`, `underfitting`, `high bias`

---

### Model B

**Answer：**
Model B may have lower training error but higher testing error due to overfitting.

**中文解釋：**
Degree 10 太複雜，可能連 noise 都 fit 埋，所以 training error 細，但 testing error 可能大。

**English keywords：**
`degree 10 polynomial`, `overfitting`, `high variance`

---

### Which has better training performance?

**Answer：**
Model B likely has smaller training error.

**中文解釋：**
因為 Model B capacity 大，可以 fit 到更多 training data pattern，甚至 noise。

**English keywords：**
`model capacity`, `training error`, `complex model`

---

### How to improve Model B?

**Answer：**

* Regularization
* Reduce polynomial degree
* Cross-validation
* More training data
* Early stopping, if iterative training

**English keywords：**
`regularization`, `L1`, `L2`, `cross-validation`, `model complexity`

---

## Q：How to solve underfitting?

| Model               | Method                                                       |
| ------------------- | ------------------------------------------------------------ |
| Logistic Regression | Add polynomial / interaction features, reduce regularization |
| Random Forest       | Increase tree depth, increase number of trees                |
| SVM                 | Use RBF kernel, tune C / gamma                               |
| Neural Network      | Add layers / neurons, train longer                           |

**中文解釋：**
Underfitting 通常代表 model 太簡單，所以要增加 model capacity，或者減少太強嘅 regularization。

**English keywords：**
`increase model capacity`, `reduce regularization`, `RBF kernel`, `deeper network`

---

## Q：How to solve overfitting in CNN?

**Answer：**

* Dropout
* Data augmentation
* L2 regularization
* Early stopping
* Reduce model complexity

**中文解釋：**
CNN 太複雜時，好容易記住 training images / sequence pattern。Dropout 可以迫 model 唔好太依賴某幾個 neurons；data augmentation 可以令 training data 更多樣化。

**English keywords：**
`CNN`, `dropout`, `data augmentation`, `L2 regularization`, `early stopping`

---

# 4. Model Comparison：KNN / Decision Tree / Random Forest / Logistic Regression / SVM / Naïve Bayes

## 出現位置

| Exam Paper | Question                                                                   |
| ---------- | -------------------------------------------------------------------------- |
| 2022 paper | Q1: SVM RBF vs Logistic Regression removing far correctly classified point |
| 2023 paper | Q5: Decision Tree / Random Forest / overfitting                            |
| 2024 paper | Q1: outliers；Q2: parameters / hyperparameters；Q4: Decision Tree vs KNN     |
| 2025 paper | Q1: parameters / hyperparameters for many models                           |

---

## Q：Remove a correctly classified point far from boundary — effect on SVM RBF?

**Answer：**
Little or no effect on decision boundary.

**中文解釋：**
SVM decision boundary 主要由 support vectors 決定。遠離 boundary 而且正確分類嘅 point 通常唔係 support vector，所以移除佢後，boundary 幾乎唔變。

**English keywords：**
`Support Vector Machine`, `RBF kernel`, `support vectors`, `decision boundary`, `margin`

---

## Q：Remove a correctly classified point far from boundary — effect on Logistic Regression?

**Answer：**
May have small effect because logistic regression uses all training samples to estimate parameters.

**中文解釋：**
Logistic regression 係用所有 data points 去 minimize loss。即使某個 point 遠離 boundary，佢對 loss 嘅貢獻可能細，但唔係完全無影響。

**English keywords：**
`Logistic Regression`, `loss function`, `all data points`, `parameters`, `decision boundary`

---

## Q：Decision Tree vs KNN computational complexity

### Training

**Answer：**

* Decision Tree：training cost higher because it needs to search for best splits.
* KNN：training almost no cost; it just stores training data.

**中文解釋：**
KNN 係 lazy learning，training 時幾乎只係記住 data。
Decision Tree 要計每個 feature 點樣 split 最好，所以 training 較重。

**English keywords：**
`Decision Tree`, `KNN`, `training complexity`, `lazy learning`, `split criterion`

---

### Testing

**Answer：**

* Decision Tree：fast; just follow split rules from root to leaf.
* KNN：slow; needs to compute distance from test sample to many training samples.

**中文解釋：**
Decision Tree predict 時好似行 if-else tree。
KNN predict 時要同 training data 入面好多 points 計 distance，所以 test time 較慢。

**English keywords：**
`testing complexity`, `nearest neighbors`, `distance computation`, `tree traversal`

---

## Q：Decision Tree advantage over KNN

**Answer：**

* More interpretable
* Faster prediction
* Handles feature scaling better
* Can identify decision rules

**中文解釋：**
Decision Tree 可以睇到「如果 feature A < threshold，就去左邊」呢啲 rule，容易解釋畀人聽。

**English keywords：**
`interpretability`, `decision rules`, `fast prediction`, `threshold split`

---

## Q：KNN advantage over Decision Tree

**Answer：**

* Simple to implement
* Non-parametric
* Can model complex local decision boundaries
* No explicit training stage

**中文解釋：**
KNN 唔需要假設 data distribution，對某啲 local pattern 可以好自然咁分類。

**English keywords：**
`non-parametric`, `local decision boundary`, `lazy learning`, `instance-based learning`

---

## Q：Decision Tree prone to overfitting?

**Answer：**
Yes. Decision trees can easily overfit if they grow too deep.

**中文解釋：**
一棵 tree 如果不斷 split 到每個 leaf 得好少 samples，佢會記住 training data 入面嘅 noise。

**English keywords：**
`Decision Tree`, `overfitting`, `tree depth`, `pruning`, `leaf nodes`

---

## Q：How to build Random Forest from Decision Trees?

**Answer：**

1. Generate multiple bootstrap samples from training data
2. Train one decision tree on each bootstrap sample
3. At each split, randomly select a subset of features
4. Combine predictions by majority voting / averaging

**中文解釋：**
Random Forest 係好多棵 decision tree 嘅 ensemble。每棵 tree 見到嘅 data 同 features 都有少少唔同，最後投票決定答案。

**English keywords：**
`Random Forest`, `bootstrap sampling`, `bagging`, `feature randomness`, `majority voting`, `ensemble learning`

---

## Q：Random Forest hyperparameters

**Answer examples：**

* Number of trees
* Maximum tree depth
* Minimum samples per leaf
* Number of features considered at each split

**English keywords：**
`n_estimators`, `max_depth`, `min_samples_leaf`, `max_features`

---

# 5. Parameters vs Hyperparameters

## 出現位置

| Exam Paper | Question                                                                                              |
| ---------- | ----------------------------------------------------------------------------------------------------- |
| 2024 paper | Q2: Logistic Regression parameter / hyperparameter；Naïve Bayes parameter；Random Forest hyperparameter |
| 2025 paper | Q1: parameter and hyperparameter for LR, NB, RF, SVM, CNN, K-means                                    |

---

## Core concept

**Parameter：**
Model learns from training data.

**Hyperparameter：**
Set before training; not directly learned from data.

**中文解釋：**
Parameter 係 model 自己學返嚟。
Hyperparameter 係人手設定，或者用 validation / grid search 揀。

**English keywords：**
`parameters`, `hyperparameters`, `learned from data`, `set before training`, `model tuning`

---

## Common examples

| Model               | Parameter                            | Hyperparameter                               |
| ------------------- | ------------------------------------ | -------------------------------------------- |
| Logistic Regression | weights (w), bias (b)                | learning rate, regularization strength C     |
| Naïve Bayes         | class prior, mean, variance          | smoothing parameter                          |
| Random Forest       | split rules inside trees             | number of trees, max depth                   |
| SVM                 | support vectors, learned weights     | C, gamma, kernel type                        |
| CNN                 | convolution filters, weights, biases | number of layers, kernel size, learning rate |
| K-means             | cluster centroids                    | number of clusters K                         |

**中文解釋：**
例如 logistic regression 入面，weight 係 training 學出嚟；但 learning rate 係你訓練前設定。
K-means 入面，cluster centroid 係 algorithm 學到；但 K，即係分幾多群，要你預先決定。

**English keywords：**
`weights`, `bias`, `class prior`, `centroids`, `C`, `gamma`, `kernel size`, `number of clusters`

---

# 6. Neural Network / Backpropagation / Learning Rate / Activation Function

## 出現位置

| Exam Paper | Question                                                                               |
| ---------- | -------------------------------------------------------------------------------------- |
| 2022 paper | Q5: NN parameters, Network A vs B, backpropagation, learning rate, activation function |
| 2023 paper | Q2/Q4: NN learning curve, backpropagation, learning rate selection                     |
| 2024 paper | Q5: NN structure parameter count, overfitting/underfitting, non-linearity              |
| 2025 paper | Q3: embedding layer, CNN parameters, fully connected parameters                        |

---

## Q：Backpropagation basic idea

**Answer：**
Backpropagation computes gradients of the loss with respect to each parameter using the chain rule, then updates weights using an optimizer such as gradient descent.

**中文解釋：**
NN 先 forward pass 出 prediction，計 loss。
之後 backward pass 將 error 由 output layer 傳返去前面 layers，計每個 weight 應該點改。

**English keywords：**
`backpropagation`, `chain rule`, `gradient`, `loss function`, `gradient descent`, `weight update`

---

## Q：Learning rate role

**Answer：**
Learning rate controls the step size of parameter updates.

* Too large：training unstable, may diverge
* Too small：training slow, may get stuck
* Suitable：stable convergence

**中文解釋：**
Learning rate 好似每次落山行幾大步。
行太大步會越過最低點；行太細步會好慢。

**English keywords：**
`learning rate`, `step size`, `convergence`, `divergence`, `gradient descent`

---

## Q：How to select learning rate?

**Answer：**

* Validation set
* Grid search
* Learning rate schedule
* Learning rate finder

**中文解釋：**
可以試幾個 learning rate，例如 0.1、0.01、0.001，睇 validation loss 邊個最好。

**English keywords：**
`grid search`, `validation loss`, `learning rate schedule`, `hyperparameter tuning`

---

## Q：Purpose of activation function

**Answer：**
Activation function introduces non-linearity to the neural network.

**中文解釋：**
如果無 activation function，無論有幾多 layers，成個 neural network 都只係 linear model。
有 ReLU / sigmoid / tanh 後，NN 先可以學複雜 pattern。

**English keywords：**
`activation function`, `non-linearity`, `ReLU`, `sigmoid`, `tanh`

---

## Q：Parameter count examples

### 2022 Network A

Input = 10, hidden = 50, output = 2

[
(10 \times 50 + 50) + (50 \times 2 + 2) = 652
]

**Answer：652 parameters**

**English keywords：**
`weights`, `biases`, `fully connected layer`, `parameter count`

---

### 2024 Structure 1

Input = 10, hidden layers = 200 + 200, output = 10

[
(10 \times 200 + 200) + (200 \times 200 + 200) + (200 \times 10 + 10)
]

[
= 2200 + 40200 + 2010 = 44410
]

**Answer：44,410 parameters**

---

### 2025 Embedding Layer

Characters：

* a-z = 26
* digits 0-9 = 10
* special characters `_`, `-`, `%` = 3

Vocabulary size = 39
Embedding dimension = 100

[
39 \times 100 = 3900
]

**Answer：3,900 parameters**
如果 padding token 都計入 vocabulary，則係：

[
40 \times 100 = 4000
]

**中文解釋：**
Embedding layer 係將每個 character 轉成 100-dimensional vector。每個 character 都有自己一組 embedding weights。

**English keywords：**
`embedding layer`, `vocabulary size`, `embedding dimension`, `character tokenization`

---

### 2025 Model A：1D CNN

Input channel = 100
Kernel size = 2
Filters = 3

Each filter：

[
2 \times 100 + 1 = 201
]

Total：

[
201 \times 3 = 603
]

**Answer：603 parameters**

**English keywords：**
`1D CNN`, `filter`, `kernel size`, `bias`, `local pattern`

---

### 2025 Model B：Flatten + Fully Connected 50 neurons

Input length = 45
Embedding dimension = 100

Flatten size：

[
45 \times 100 = 4500
]

Fully connected：

[
4500 \times 50 + 50 = 225050
]

**Answer：225,050 parameters**

**中文解釋：**
Fully connected layer 會連接所有 input positions，所以 parameters 非常多。CNN 只睇 local windows，所以 parameters 少好多。

**English keywords：**
`flatten layer`, `fully connected layer`, `dense layer`, `parameter efficiency`

---

# 7. Phishing Detection

## 出現位置

| Exam Paper | Question                                                                                      |
| ---------- | --------------------------------------------------------------------------------------------- |
| 2022 paper | Q7: URL / domain / page content features；deep learning URL vectors                            |
| 2023 paper | Q6: phishing URL example；URL-based / domain-based / page-content features；deep learning input |
| 2024 paper | Q6: ML vs blacklist；URL features；supervised limitation；embedding layer                        |
| 2025 paper | Q6: URL features；domain feature；traditional ML vs DL；false positive / false negative          |

---

## Q：URL-based features examples

**Answer examples：**

* URL length
* Number of dots
* Number of subdomains
* Use of IP address
* Use of suspicious characters
* Presence of brand name with typo, e.g. `faceb00k`
* Number of digits
* Number of special symbols
* Use of `http` instead of `https`

**中文解釋：**
Phishing URL 通常會扮真網站，例如用 `faceb00k` 代替 `facebook`，或者用好長、好多亂碼嘅 URL 令 user 睇唔清楚。

**English keywords：**
`URL-based features`, `URL length`, `subdomain`, `special characters`, `typosquatting`, `HTTPS`

---

## Q：Domain-based feature example

**Answer：Domain age**

**中文解釋：**
Phishing domain 通常係新註冊，用完就棄，所以 domain age 好短可能係 suspicious signal。
可以用 WHOIS database 查 domain creation date，然後轉成 numerical feature，例如 domain age in days。

**English keywords：**
`domain age`, `WHOIS`, `creation date`, `domain-based feature`, `numerical feature`

---

## Q：Page-content feature example

**Answer examples：**

* Number of external links
* Presence of login form
* Mismatch between displayed link and actual link
* Number of password input fields
* Similarity to legitimate webpage

**中文解釋：**
Phishing page 通常會有 login form 偷 account/password，或者 content 扮銀行 / social media login page。
可以將「有冇 password field」變成 0/1 feature，或者計 external link ratio。

**English keywords：**
`page-content features`, `login form`, `password field`, `external links`, `HTML features`

---

## Q：URL feature vs Domain feature

| Feature Type   | Advantage                         | Disadvantage                          |
| -------------- | --------------------------------- | ------------------------------------- |
| URL feature    | Easy and fast to extract          | May miss sophisticated phishing       |
| Domain feature | Can capture registration behavior | Need external lookup, e.g. WHOIS      |
| Page content   | Rich information                  | Need access webpage, slower and risky |

**中文解釋：**
URL feature 只要有條 URL 就計到，所以最快。
Domain feature 要查 domain database。
Page content 要下載網頁，可能慢，而且網站可能 offline 或有安全風險。

**English keywords：**
`URL features`, `domain features`, `page-content features`, `feature extraction cost`

---

## Q：Machine learning vs blacklist in phishing detection

**Answer：**
Machine learning can detect new unseen phishing URLs based on patterns, while blacklist can only detect known malicious URLs.

**中文解釋：**
Blacklist 好似「壞網站名單」。如果條 phishing URL 未入名單，就捉唔到。
ML 可以睇 URL pattern，例如長度、符號、domain age，即使未見過都可能 detect 到。

**English keywords：**
`blacklist`, `machine learning`, `zero-day phishing`, `generalization`, `unseen URLs`

---

## Q：Limitation of supervised phishing detection

**Answer：**

* Requires labeled data
* New phishing techniques may differ from training data
* Dataset may become outdated
* Class imbalance may occur

**中文解釋：**
Supervised learning 要有 label，例如 phishing / legitimate。但 phishing 手法變得快，training data 舊咗會令 model detect 唔到新攻擊。

**English keywords：**
`supervised learning`, `labeled data`, `concept drift`, `class imbalance`, `dataset bias`

---

## Q：Deep learning URL numerical vectors

**Answer examples：**

1. **Character-level one-hot encoding**
   每個 character 變成 one-hot vector。

2. **Character embedding**
   每個 character 變成 dense vector，由 model 學出。

**Comparison：**

| Method    | Advantage                               | Disadvantage                            |
| --------- | --------------------------------------- | --------------------------------------- |
| One-hot   | Simple, no training needed              | Sparse, high-dimensional                |
| Embedding | Dense, can learn character relationship | Needs training data, less interpretable |

**中文解釋：**
One-hot 係每個 character 用一條好長 vector 表示，只有一格係 1。
Embedding 係 model 自己學每個 character 嘅 dense representation，通常更適合 deep learning。

**English keywords：**
`one-hot encoding`, `character embedding`, `dense vector`, `sparse vector`, `URL representation`

---

## Q：Embedding layer purpose in CNN phishing detection

**Answer：**
Embedding layer converts discrete characters or tokens into dense numerical vectors that can be processed by CNN.

**中文解釋：**
CNN 唔可以直接食文字 URL，所以要先將 character 轉成數字 vector。Embedding layer 就係學每個 character/token 嘅 vector representation。

**Why not usually in RF / SVM?**
Traditional ML models usually use manually engineered numerical features, not trainable embedding layers.

**English keywords：**
`embedding layer`, `CNN`, `dense representation`, `feature engineering`, `traditional ML`

---

## Q：False Negative / False Positive in phishing detection

**False Negative：**
A phishing URL is classified as legitimate.

**Impact：**
User may click phishing site and lose credentials. Cybersecurity risk is high.

**False Positive：**
A legitimate URL is classified as phishing.

**Impact：**
User inconvenience, blocked normal website, lower trust in system.

**Which is more important?**
Usually false negative is more critical in cybersecurity because missing a real phishing attack can cause account compromise or data loss.

**English keywords：**
`false negative`, `false positive`, `phishing detection`, `security risk`, `user inconvenience`

---

# 8. Malware Detection

## 出現位置

| Exam Paper | Question                                                                               |
| ---------- | -------------------------------------------------------------------------------------- |
| 2022 paper | Q6: obfuscation, static/dynamic API features, numerical vectors, ML vs DL              |
| 2023 paper | Q7: opcode sequences, static/dynamic analysis, malware images                          |
| 2024 paper | Q7: obfuscation, API sequences, static/dynamic, numerical vectors                      |
| 2025 paper | Q5: imbalanced malware dataset, autoencoder；Q7: PE header, static/dynamic, obfuscation |

---

## Q：What is malware obfuscation?

**Answer：**
Malware obfuscation means modifying malware code to hide its malicious behavior while keeping functionality.

**Examples：**

* Code packing
* Encryption
* Renaming variables/functions
* Inserting junk code
* Control flow obfuscation

**中文解釋：**
Obfuscation 係 malware 作者將惡意程式「扮到唔似原本樣」，避開 detection。例如將 code 加密、壓縮、插入無用指令，令 static scanner 難睇出真正行為。

**English keywords：**
`malware obfuscation`, `packing`, `encryption`, `junk code insertion`, `control flow obfuscation`, `evasion`

---

## Q：Static analysis vs Dynamic analysis

| Analysis         | Meaning                                     | Advantage                     | Disadvantage                          |
| ---------------- | ------------------------------------------- | ----------------------------- | ------------------------------------- |
| Static analysis  | Analyze file without running it             | Fast, safe, scalable          | Weak against packing / obfuscation    |
| Dynamic analysis | Run malware in sandbox and observe behavior | Better for obfuscated malware | Slow, risky, may miss hidden behavior |

**中文解釋：**
Static analysis 係唔執行 malware，只睇 file、code、PE header、opcode、API imports。
Dynamic analysis 係放入 sandbox 執行，觀察佢實際 call 咩 API、改咩 registry、連咩 network。

**English keywords：**
`static analysis`, `dynamic analysis`, `sandbox`, `API calls`, `opcode`, `PE header`

---

## Q：Would static and dynamic analysis give same API / opcode sequence?

**Answer：**
Not necessarily.

**中文解釋：**
Static analysis 可能睇到所有 imported API 或 possible opcode，但 dynamic analysis 只會見到實際執行路徑。
如果 malware 有 conditional behavior，例如檢測到 sandbox 就唔執行惡意行為，dynamic analysis 可能睇唔到完整 sequence。

**English keywords：**
`API sequence`, `opcode sequence`, `execution path`, `conditional behavior`, `sandbox evasion`

---

## Q：Which is better for obfuscated malware?

**Answer：**
Dynamic analysis is usually better because it observes runtime behavior after unpacking or decryption.

**中文解釋：**
Obfuscated malware 可能靜態睇落係加密 / packed，睇唔到真正 code。但執行時 malware 要解密同運行，所以 dynamic analysis 有機會見到真實行為。

**English keywords：**
`runtime behavior`, `unpacking`, `decryption`, `dynamic analysis`, `obfuscated malware`

---

## Q：Numerical feature vectors from API / opcode sequences

**Answer examples：**

1. **Bag-of-words / frequency vector**
   Count how many times each API / opcode appears.

2. **N-gram vector**
   Count sequences of length n, e.g. API bigrams or trigrams.

3. **Sequence encoding / embedding**
   Map each API/opcode to integer or embedding vector for deep learning.

**Comparison：**

| Method               | Advantage                       | Disadvantage                 |
| -------------------- | ------------------------------- | ---------------------------- |
| Frequency vector     | Simple, easy for ML models      | Loses order information      |
| N-gram               | Keeps local order pattern       | High dimensional             |
| Embedding / sequence | Good for DL, preserves sequence | Needs more data and training |

**中文解釋：**
Frequency vector 只知 `CreateFile` 出現幾多次，但唔知順序。
N-gram 可以知道 `OpenFile → WriteFile → CloseFile` 呢類行為 pattern，更有 malware behavior 意義。

**English keywords：**
`API sequence`, `opcode sequence`, `bag-of-words`, `frequency vector`, `n-gram`, `embedding`

---

## Q：ML vs DL in malware detection

**Traditional ML：**

* Needs manual feature engineering
* Example：API frequency, opcode n-gram, PE header features
* Model：SVM, Random Forest, Logistic Regression

**Deep Learning：**

* Can learn features automatically
* Input can be sequence, image, bytes, embeddings
* Model：CNN, RNN, LSTM, Transformer

**中文解釋：**
Machine learning 通常要人手設計 feature。
Deep learning 可以直接用 bytes / sequence / image，自己學 pattern，但需要更多 data 同 computation。

**English keywords：**
`traditional machine learning`, `deep learning`, `feature engineering`, `automatic feature learning`, `CNN`, `RNN`

---

## Q：PE header features for malware detection

**Answer examples：**

* Number of sections
* Section entropy
* Imported DLLs
* Imported API functions
* Entry point address
* File size
* Timestamp
* Characteristics flags

**中文解釋：**
PE header 係 Windows executable file 入面嘅 metadata。Malware 可能有異常 section entropy、可疑 imports、奇怪 entry point，所以可以用嚟 detect malware。

**English keywords：**
`PE header`, `section entropy`, `import table`, `DLL`, `entry point`, `metadata`

---

## Q：Malware image representation

**Answer：**

1. Convert raw bytes into grayscale image
   Each byte value 0–255 becomes pixel intensity.

2. Arrange bytes into fixed-width 2D matrix
   Then resize / pad / crop to fixed image size for CNN.

**中文解釋：**
Malware file 可以當成一串 byte。每個 byte 轉成一個 pixel，就可以形成 image。CNN 可以學 malware family 嘅 visual pattern。

**English keywords：**
`malware image`, `raw bytes`, `grayscale image`, `CNN`, `byte-to-image conversion`

---

# 9. Imbalanced Data / Autoencoder

## 出現位置

| Exam Paper | Question                                                                      |
| ---------- | ----------------------------------------------------------------------------- |
| 2025 paper | Q5: 100,000 benign vs 100 malware；binary classification challenge；autoencoder |

---

## Q：Challenge of 100,000 benign vs 100 malware

**Answer：**

This is a severe class imbalance problem.

Likely result：

* Model predicts most samples as benign
* High overall accuracy but poor malware detection
* Low recall for malware
* Many false negatives

**中文解釋：**
如果 100,100 個 samples 入面得 100 個 malware，model 就算全部估 benign，都有接近 99.9% accuracy，但其實完全 detect 唔到 malware。所以 accuracy 會 misleading。

**English keywords：**
`class imbalance`, `minority class`, `majority class`, `false negative`, `recall`, `misleading accuracy`

---

## Q：Autoencoder architecture

**Answer：**

An autoencoder contains：

1. Encoder：compress input into latent representation
2. Bottleneck：low-dimensional compressed code
3. Decoder：reconstruct original input
4. Reconstruction loss：measure difference between input and output

**中文解釋：**
Autoencoder 目標係學識重建 input。正常 data 見得多，就重建得好；異常 data 見得少，就重建得差。

**English keywords：**
`autoencoder`, `encoder`, `decoder`, `bottleneck`, `latent representation`, `reconstruction loss`

---

## Q：How autoencoder addresses imbalanced malware detection?

**Answer：**

Train the autoencoder mainly on benign samples.
During testing：

* Benign sample → low reconstruction error
* Malware / anomaly → high reconstruction error

Set a threshold on reconstruction error to detect malware.

**中文解釋：**
因為 benign samples 好多，可以用佢哋 train autoencoder 學「正常樣」。
Malware 因為同正常 pattern 唔同，autoencoder 重建唔好，error 高，就可以當 anomaly detect 出嚟。

**English keywords：**
`anomaly detection`, `benign training`, `reconstruction error`, `threshold`, `unsupervised learning`

---

# 10. 高頻出題 Topic 排名

根據 4 份卷，最常出現嘅 topic：

| Rank |                                               Topic |       出現頻率 | 必溫程度 |
| ---- | --------------------------------------------------: | ---------: | ---- |
| 1    |     Phishing detection features / URL / domain / DL | 4/4 papers | 極高   |
| 2    | Malware detection / static vs dynamic / obfuscation | 4/4 papers | 極高   |
| 3    |               Feature selection / filter vs wrapper | 3/4 papers | 高    |
| 4    |                   Normalization / model sensitivity | 3/4 papers | 高    |
| 5    |             Overfitting / underfitting / validation | 4/4 papers | 極高   |
| 6    |         Neural network / parameter count / backprop | 4/4 papers | 極高   |
| 7    |                       Parameters vs hyperparameters | 2/4 papers | 中高   |
| 8    |      KNN / Decision Tree / Random Forest comparison | 3/4 papers | 高    |

---

# 11. Exam 答題模板

## Template 1：Compare two methods

**Answer structure：**

```text
The major difference is ...
Method A ...
Method B ...
The advantage of A is ...
The limitation of A is ...
Therefore, A is better when ..., while B is better when ...
```

**中文用法：**
比較題一定要講：

1. 定義
2. 分別
3. 優點
4. 缺點
5. 適用情況

---

## Template 2：Explain why feature useful

```text
This feature is useful because phishing/malware samples often show ...
It can be converted into a numerical feature by ...
For example, ...
However, one limitation is ...
```

**適合：**

* URL feature
* Domain feature
* Page content feature
* API feature
* PE header feature

---

## Template 3：Overfitting / underfitting

```text
The model is likely to suffer from overfitting / underfitting.
This is because ...
As a result, the training error is ..., while the testing error is ...
One possible solution is ...
```

---

## Template 4：Static vs Dynamic malware analysis

```text
Static analysis examines the file without execution.
Dynamic analysis executes the file in a controlled environment.
They may not produce the same features because ...
Static analysis is faster and safer, but weaker against obfuscation.
Dynamic analysis is better for observing runtime behavior, but slower and may suffer from sandbox evasion.
```

---

# 12. 最後溫習建議

最值得放入 cheat sheet 嘅英文 keywords：

```text
Feature normalization
Min-Max Scaling
Standardization
KNN distance-based
Decision Tree threshold split
Filter-based feature selection
Wrapper-based feature selection
Overfitting
Underfitting
Generalization
Regularization
Backpropagation
Learning rate
Activation function
Embedding layer
CNN
URL-based features
Domain-based features
Page-content features
Blacklist
False positive
False negative
Malware obfuscation
Static analysis
Dynamic analysis
API sequence
Opcode sequence
PE header
Autoencoder
Reconstruction error
Class imbalance
```

呢 4 份卷最大規律係：**Section A 考 ML 基礎概念，Section B 考 cybersecurity application。**
所以溫習時唔好只背 model，要識將 ML concept 套落 **phishing detection** 同 **malware detection**。
