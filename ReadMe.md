# jFaces - What is that?

jFaces is simple Fisherfaces algorithm in Java. This is a re-written project from pure MATLAB code to Java code. The pourpose with this project is to offer a simple setup of an algorithm that can do image classification.
Fisherfaces is the best algorithm to use if you have a smal amout of data, as 10 pictures of 10 people where the pictrures have not the best resolution. 
You don't need pictures, you can use normal data as well. 

# What should I use jFaces for?

Assume that you want you are creating a web application that going to train a lot of data. But training a lot of data takes time.
You need a faster tool that requries less tuning. 

# How do I use this software?

Open your terminal and locate the "jFaces.jar" file. Call it as this:

Predicting on random sample with k-value 4
```
java -Xmx1000M -jar jFaces.jar -f "/home/dell/FisherFaces-Examples/pictures/Yale Database sample" -m P1 -k 4
```
Predicting on sample 10 with k-value 10
```
java -jar jFaces.jar -f "/home/dell/FisherFaces-Examples/pictures/Yale Database sample" -m P2 40 -k 10
```
Validating on on all samples with k-value 8. This takes time!
```
java -jar jFaces.jar -f "/home/dell/FisherFaces-Examples/pictures/Yale Database sample" -m V1 -k 8
```
Quick validating on one sample per each subject with k-value 2
```
java -jar jFaces.jar -f "/home/dell/FisherFaces-Examples/pictures/Yale Database sample" -m V2 -k 2
```
If you an error about heap memory you can always increase it by using the flag `-Xmx<memory>M` as in megabyte
```
java -Xmx1000M -jar jFaces.jar -f "/home/dell/FisherFaces-Examples/pictures/Yale Database sample" -m P1 -k 4
```

# TODO

- Functionality to seralize the fisher faces object model and use it later with random pictures with the `Fisherfaces.predict` function. See Main.java file. 
- Determine how good k-nearest neighbor was

# Will this project work on Android and Iphone with GraalVM?

Yes. This project is 100% Java. No external C/C++/Python libraries. Only Java. 

# Development - What do I need?

To run work on this project, you need the following software:

- OpenJDK 11
- Eclipse IDE

Don't forget to use low quality pictures like Yale database or JAFFE database. jFaces works great with them.

# Where is the orginal source and validation?

Here you can find a fully working report/sample where I have used ByteFish's image classification library. I have tried it out onto some few databases.
It works great!
https://github.com/DanielMartensson/FisherFaces-Examples/


# Tested on databases

- Yale database: http://vision.ucsd.edu/content/yale-face-database
- JAFFE database: https://zenodo.org/record/3451524#.XsKKkhZS_J8

