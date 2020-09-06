# jFaces - What is that?

jFaces is simple Fisherfaces algorithm in 100% Java ☕ (No C/C++/Pyhton/Fortran/etc..). The purpose with this project is to offer a simple setup of an algorithm that can do image classification.
Fisherfaces is the best algorithm to use if you have a smal amout of data, as 10 pictures of 10 people where the pictrures have not the best resolution. 
You don't need pictures, you can use normal data as well. 

## How do I use this software?

Begin first to install at least OpenJDK 11. I have left OpenJDK 8 for a long time ago.
Collect pictures of the same individual into one folder. Do the same for the rest of the individuals into their own folders.
Every folder are going to get a ID number beginning from 0 and counting up.
If you compare a unknown picture that belongs to one of these folders, then jFaces is going to give you the ID number of that unknown picture.

Structure the folders as subject in this way. 

- Subject00
  - SteveHappy.png
  - SteveSad.png
  - SteveAngry.png
  - SteveMustach.png
  - SteveGlasses.png
  - etc...
- Subject01
  - BadAnna.png
  - DirtyAnna.png
  - CleanAnna.png
  - SadAnna.png
  - AnnaCrossEyed.png
  - AnnaWithAHat.png
  - etc...
- Subject02
  - HansDumb.png
  - HansRedFace.png
  - etc...
- Subject03
  - Jenny_Angry.png
  - Jenny_Normal.png
  - Jenny_with_hat.png
  - Jenny_dark.png
- SubjectXXXX
  - etc...

Open your terminal and locate the "jFaces.jar" file. Call it as this:

```
dell@dell-Precision-M6400:~/Dokument/GitHub/jFaces$ java -jar jFaces.jar --help
jFaces - Image classifiction in Java. Made by Daniel Mårtensson
Call jFaces.jar with: 'java -jar jFaces.jar' followed by the commands below
Example for increasing the heap memory: 'java -Xmx1000M -jar jFaces.jar' for 1000 megabyte in heap memory
Command list:
	-f Path to the folder that contains subfolders with pictures
	-m Method which prediction or validation to use, followed by 'P1', 'P2', 'V1', 'V2' or 'V3'
		P1 = Prediction on random sample
		P2 = Prediction on selected sample, followed by sample number
		V1 = Cross validation on classes
		V2 = Cross validation on samples
		V3 = Test and train data validation, followed by value ration between 0 < ratio < 1
	-b Build model and save it, followed by path to the save location
	-l Load model, followed by path to the model location
	-k K-nearest value, followed by a positive integer k-value
Examples:
	Random prediction with k = 30: -f '/path/to/subfolers/with/pictures' -m P1 -k 30
	Selected sample 10 at prediction with k = 30: -f '/path/to/subfolers/with/pictures' -m P2 10 -k 30
	Cross validation on classes with k = 20: -f '/path/to/subfolers/with/pictures' -m V1 -k 20
	Cross validation on samples with k = 20: -f '/path/to/subfolers/with/pictures' -m V2 -k 20
	Test 30% and train 70% set validation with k = 10: -f '/path/to/subfolers/with/pictures' -m V3 0.7 -k 10
	Build model: -f '/path/to/subfolers/with/pictures' -b '/path/to/model/location.ser'
	Build model and do validation on samples: -f '/path/to/subfolers/with/pictures' -b '/path/to/model/location.ser' -m V2 -k 20
	Load model and do validation on classes: -f '/path/to/subfolers/with/pictures' -l '/path/to/model/location.ser' -m V1 -k 20
dell@dell-Precision-M6400:~/Dokument/GitHub/jFaces$ 
```

This project have a built in C-code generation if you want to apply this onto embedded systems.
When you build a fisherfaces model with parameter `-b`, then you are going to recieve an example, model and the pictures you have collected, in C-code. 
This C-code have the ability to measure how likely the pictures are identified. The pourpose with the Java version is just to build the model and validate it. 
The C code is the practical implementation. 

## Status of the project

This project is 100% done. No errors. Just copy the code to your project if you want image classification in the easy way.

## Tested on databases

- Yale database: http://vision.ucsd.edu/content/yale-face-database
- JAFFE database: https://zenodo.org/record/3451524#.XsKKkhZS_J8
- MNIST database: http://yann.lecun.com/exdb/mnist/

| Database     |  Validation | Comment | 
| ------------:|------------:|--------:|
|  Yale        |  97%        | k = 4, Cross validation on samples   |
|  JAFFE       |  98.6%      | k = 10, Cross validation on samples  |
|  MNIST       |  90%        | k = 4, Test set validation  60% train and 40% test data |
