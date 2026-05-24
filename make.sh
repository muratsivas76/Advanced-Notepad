#!/bin/bash

clear

# 1. Clean previous build
echo "Cleaning old JAR file..."
rm -fv bin/xnotdefteri_190426.jar

# 2. Compile source code
echo "Compiling: javac -source 1.8 -target 1.8 ..."
javac -source 1.8 -target 1.8 -sourcepath src -cp obj -g:none -proc:none -nowarn -O -d obj src/net/dilmerkezi/defter/NotDefteriX.java

# 3. Create JAR with Manifest
echo "Packaging JAR: jar cmf MANIFEST.MF ..."
jar cmf MANIFEST.MF bin/xnotdefteri_190426.jar -C obj net -C . src tool compile_run.txt xcopy.sh ControlChanges.* changes.txt

# 4. Cleanup object files
echo "Cleaning up object files..."
rm -rf obj/net

# 5. Run the application
echo "Launching: java -jar bin/xnotdefteri_190426.jar"
java -jar bin/xnotdefteri_190426.jar &
