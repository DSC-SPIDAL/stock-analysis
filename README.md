# stock-analysis
-v /home/supun/dev/projects/dsspidal/data/sample_in -d /home/supun/dev/projects/dsspidal/data/sample_out
-v /home/supun/dev/projects/dsspidal/data/2007_2014 -d /home/supun/dev/projects/dsspidal/data/2007_2014_out


java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar DistanceCalculator -v /home/supun/dev/projects/dsspidal/data/2007_2014 -d /home/supun/dev/projects/dsspidal/data/2007_2014_out
java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar VectorGenerator -i /home/supun/dev/projects/dsspidal/data/2004_2014/stock_2004_2014.csv -o /home/supun/dev/projects/dsspidal/data/2004_2014/vectors -s 20040101 -e 20140101 -d 30

java -cp mpi/target/stocks-1.0-ompi1.8.1-jar-with-dependencies.jar VectorGenerator -i /home/supun/dev/projects/dsspidal/data/2004_2014/2004_2014.csv -o /home/supun/dev/projects/dsspidal/data/2004_2014/vectors -s 20040101 -e 20150101 -d 370
