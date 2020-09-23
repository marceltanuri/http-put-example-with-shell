#!/bin/bash
helpFunction()
{
   echo ""
   echo "Usage: $0 -m parameterM -e parameterE"
   echo -e "\t-m ElasticSearch max results property."
   echo -e "\t-e Target environment, dev for localhost, hk for homologation, prd for production. "
   exit 1 # Exit script after printing help
}

while getopts "m:e:" opt
do
   case "$opt" in
      m ) parameterM="$OPTARG" ;;
      e ) parameterE="$OPTARG" ;;
      ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
   esac
done

# Print helpFunction in case parameters are empty
if [ -z "$parameterM" ] || [ -z "$parameterE" ]
then
   echo "Some or all of the parameters are empty";
   helpFunction
fi

script_dir=$(dirname $0)

# Begin script
echo "Initializing ElasticSearch config..."
echo "Executing in $parameterE"
echo "script dir: $script_dir"
java -jar "$script_dir"/execute-elaticsearch-config-jar-with-dependencies.jar -e$parameterE -m$parameterM> "$script_dir"/config-elaticsearch.log 2>&1 
echo "Execution has been finished. See datails in $script_dir /config-elaticsearch.log"