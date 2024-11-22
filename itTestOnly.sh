#!/usr/bin/env bash

# Function to display usage
usage() {
  echo "Usage: $0 [test-spec | all] [shared-resource]"
  echo "Example: $0 UserService controllers.ControllerSharedResource  # Runs tests matching 'UserService' using 'ControllerSharedResource'"
  echo "         $0 all controllers.ControllerSharedResource          # Runs all tests using 'ControllerSharedResource'"
  echo "         $0 UserService                                       # Runs tests matching 'UserService' without a shared resource"
  echo "Options:"
  echo "  -h, --help                Show this help message"
}

# Check if the user requested help
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  usage
  exit 0
fi

# Set default values
TESTSPEC=${1:-all}
SHARED_RESOURCE=${2:-}

# Run the tests based on the specified test spec and shared resource
if [ "$TESTSPEC" = "all" ]; then
  echo "Running all it tests"
  if [ -n "$SHARED_RESOURCE" ]; then
    sbt clean "it/testOnly * $SHARED_RESOURCE"
  else
    sbt clean it/test
  fi
else
  echo "Running it tests matching '$TESTSPEC'"
  if [ -n "$SHARED_RESOURCE" ]; then
    sbt clean "it/testOnly *$TESTSPEC* $SHARED_RESOURCE"
  else
    sbt clean "it/testOnly *$TESTSPEC*"
  fi
fi
