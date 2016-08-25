README

After unzipping the folder ML-Assignment3.zip, the folder will contain

- src (directory)
- test (test data)
- train (train data)
- report (file)
- stopWords.txt
- compressed images dir

The test folder contains test.arff and train folder contains train.arff.
These files are provided for neural networks experiments.

Please maintain this structure for execution of the program.

The following driver programs are present inside the src dir.

NBDriver.java - Naive Bayes
LRDriver.java - Logistic Regression
PerceptronDriver.java - perceptron classifier
KMeans.java - K means

Execution:

1) To execute perceptron classifier, run the driver program PerceptronDriver.java

(No arguments are required to be passed for the program).

2) To execute KMeans, run the driver program KMeans.java

(Supply arguments in this usage: <Input Image file with full absolute path> <K-value> <Output Image file name>

Sample ex: C:\Users\RahulAravind\Desktop\Books\Spring 16\Machine Learning\Assignments\Assignment3\Koala.jpg" 50 Output2_P50.jpg
