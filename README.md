# stock-analysis
-v /home/supun/dev/projects/dsspidal/data/sample_in -d /home/supun/dev/projects/dsspidal/data/sample_out
-v /home/supun/dev/projects/dsspidal/data/2007_2014 -d /home/supun/dev/projects/dsspidal/data/2007_2014_out


java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar DistanceCalculator -v /home/supun/dev/projects/dsspidal/data/2007_2014 -d /home/supun/dev/projects/dsspidal/data/2007_2014_out
java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar VectorGenerator -i /home/supun/dev/projects/dsspidal/data/2004_2014/stock_2004_2014.csv -o /home/supun/dev/projects/dsspidal/data/2004_2014/vectors -s 20040101 -e 20140101 -d 30

java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar VectorGenerator -i /home/supun/dev/projects/dsspidal/data/2004_2014/2004_2014.csv -o /home/supun/dev/projects/dsspidal/data/2004_2014/vectors -s 20040101 -e 20150101 -d 370


The workflow of Stock Analysis is as following.

### FileBreaker

FileBreaker program is used to break large stock files in to smaller files for processing. For example if we are interested in processing yearly data the stock file can be broken by year to multiple files.

#### Format of stock files

```
PermNo,Date,StockSym,Price,Volume
```
These stock files are used to create vector files. This step isn't mandotory and vector generator can be used to create vector files directly from the stock file as wel..

#### How to run



### PVectorGenerator

This program creates a file with stocks in a vector format. Each row of the file contains stock identifier, stock cap and day prices as a vector.

```
PermNo,Cap,prices.....
```

### DistanceCalculator

Produces a distance file given the vector files.


### HistoGram

Create a Histo

