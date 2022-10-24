#!/bin/bash

sudo apt-get update
sudo apt-get upgrade -y

sudo apt-get install -y wget apt-transport-https gnupg
sudo wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo apt-key add -
sudo echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list

sudo apt-get update
sudo apt-get install temurin-17-jdk -y

wget https://dlcdn.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz
sudo tar xzf apache-maven-3.8.6-bin.tar.gz -C /opt
sudo ln -s /opt/apache-maven-3.8.6 /opt/maven

maven_file="/etc/profile.d/maven.sh"
sudo touch $maven_file

echo "export JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64" >> $maven_file
echo "export M2_HOME=/opt/maven" >> $maven_file
echo "export MAVEN_HOME=/opt/maven" >> $maven_file
echo "export PATH=${M2_HOME}/bin:${PATH}" >> $maven_file

source $maven_file
