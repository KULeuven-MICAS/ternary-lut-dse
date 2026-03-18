#!/bin/bash

# Check if the ./generated folder exists
if [ -d "./generated" ]; then
    # Remove all contents of the ./generated folder
    rm -rf ./generated/*
    echo "Cleaned the ./generated folder."
else
    echo "The ./generated folder does not exist."
fi

# Check if the ./test_run_dir folder exists
if [ -d "./test_run_dir" ]; then
    # Remove all contents of the ./test_run_dir folder
    rm -rf ./test_run_dir/*
    echo "Cleaned the ./test_run_dir folder."
else
    echo "The ./test_run_dir folder does not exist."
fi