# How to Contribute

Required: Git, Maven.

```
git clone https://github.com/metricminer-msr/metricminer2.git
cd metricminer2/src/test/resources
unzip repo-1.zip
echo "path to the repo-1/ dir you just unzipped" > repo-1.txt
unzip repo-2.zip
echo "path to the repo-2/ dir you just unzipped" > repo-2.txt
```

Then, you can:

* compile : `mvn clean compile`
* test    : `mvn test`
* eclipse : `mvn eclipse:eclipse`
* build   : `mvn clean compile assembly:single`

# Creating a Study

See www.metricminer.org.br/documentation.

# License

This software is licensed under the Apache 2.0 License.

