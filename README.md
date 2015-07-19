# stock-analysis

## Stock Analysis Project

Download the source from https://github.com/iotcloud/stock-analysis and build.

```
git clone https://github.com/iotcloud/stock-analysis.git
cd stock-analysis
mvn clean install
```

## DAMDS Project

Download the project from https://github.com/DSC-SPIDAL/damds.git and build.

```
git clone https://github.com/DSC-SPIDAL/damds.git
cd damds
mvn clean install
```

##

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

