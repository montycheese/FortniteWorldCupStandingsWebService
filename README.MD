# FortniteStandings WebService

This project uses the AWS Serverless Model (SAM)

AWS Resources and configuration are specified in the cloudformation.yaml file

##Setup
Install m2e-apt
https://marketplace.eclipse.org/content/m2e-apt
Window -> Preferences -> Maven -> Annotation Processing: Select "Automatically configure JDT APT"

## Compile
mvn compile

## Build
mvn package

Outputs jar to ./targets/webservice-1.0.0.jar 

## Testing
mvn test

## Deploying to AWS
sam package --template-file template.yaml --s3-bucket deployment-us-east-1-lambdas-1 --output-template-file packaged.yaml

sam deploy --template-file ./packaged.yaml --stack-name fortnite-wc-standings --capabilities CAPABILITY_IAM

###
misc
mvn install:install-file -DgroupId=com.google.dagger -DartifactId=dagger -Dversion=2.19 -Dpackaging=jar -Dfile=/Users/montanawong/Downloads/dagger-2.19.jar
mvn install:install-file -DgroupId=com.google.dagger-compiler -DartifactId=dagger-compiler -Dversion=2.19 -Dpackaging=jar -Dfile=/Users/montanawong/Downloads/dagger-compiler-2.19.jar