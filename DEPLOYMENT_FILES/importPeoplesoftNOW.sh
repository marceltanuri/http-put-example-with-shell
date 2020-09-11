#!/bin/bash
helpFunction()
{
   echo ""
   echo "Usage: $0 -u parameterU -e parameterE"
   echo -e "\t-u Liferay credentials to access the service."
   echo -e "\t-e Target environment, hk for homologation, prd for production. "
   exit 1 # Exit script after printing help
}

while getopts "u:e:" opt
do
   case "$opt" in
      u ) parameterU="$OPTARG" ;;
      e ) parameterE="$OPTARG" ;;
      ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
   esac
done

# Print helpFunction in case parameters are empty
if [ -z "$parameterU" ] || [ -z "$parameterE" ]
then
   echo "Some or all of the parameters are empty";
   helpFunction
fi

script_dir=$(dirname $0)

# Begin script
echo "Inciando importação peoplesoft no NOW..."
echo "Executando em $parameterE"
echo "$script_dir"
java -jar "$script_dir"/execute-peopelesoft-jar-with-dependencies.jar -e$parameterE -u$parameterU> "$script_dir"/employee-import.log 2>&1 
echo "\nImportação finalizada."