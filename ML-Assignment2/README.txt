README

After unzipping the folder ML-Assignment2.zip, the folder will contain

- src (directory)
- test (test data)
- train (train data)
- report (file)
- stopWords.txt

Please maintain this structure for execution of the program.

The Driver.java is present inside the src directory.

1) Excecute Driver.java. After executing, the results will be as follows

*******************************************
Spam classifier using Naive Bayes (With Stop Words)
*******************************************
Accuracy for Ham 
97.7011 % 
Accuracy for Spam 
85.3846 % 
*******************************************
Spam classifier using Naive Bayes (Without stopWords) 
*******************************************
Accuracy for Hpam 
96.5517 % 
Accuracy for Spam 
90.7692 % 

*************************************
Spam Classifier using Logistic Regression
********************************************
Learning model parameters.......
********************************
Accuracy for Ham 
95.6897 % 
Accuracy for Spam 
86.1538 %

2) To try with different learning rate, regularization and iterations for logistic regression,

goto LogisticRegressionClassifier.java and set the following global variables,

iter = 150;
learningRate = 0.25;
regularizationParameter = 0.1;