#!/usr/bin/env bash
#
#Script para desplegar la version en el ambiente de desarrollo
#
#Por Tecnomedia

#Parametros de conexion al API de Automic
automic_client="100"
automic_username="ARA_DEV"
automic_department="AUTOMIC"
automic_pass="ARA_DEV"
automic_host="10.67.73.20"
automic_port="80"
apiPath_applications="/ara/api/data/v1/applications"
apiPath_packages="/ara/api/data/v1/packages"
apiPath_executions="/ara/api/data/v1/executions"

#Parametros de configuracion de Automic
custom_type_name="Deployment"
folder_name="PACKAGES"
workflow_name="Deploy"
deployment_profile="DEV"

main()
	{	
		VarsCreation
                if VersionType
	                then
	        		if CommitType
	                        	then
			                        if ArtifactStatus
		        		                then
								if ApplicationExist
									then
	                       						        if PackageCreate
					                                		then
	                              								if PackageStatus
						                                        		then
	        	       											if PackageDeploy
															then
																return 0	
															else
																return 1
														fi
                	               	       								else
														return 1
												fi
											else
												return 1
					       	        	                fi
									else
										return 1
								fi
							else
								return 1
						fi
					else
						return 1
				fi	
        		else
				return 1
		fi
	}
VarsCreation()
	{
		#Parametros de consulta al API de Automic
		owner_name="$automic_client/$automic_username/$automic_department"
		login_data="$owner_name:$automic_pass"

		#Parametros del artefacto en Nexus
		maven_metadata_path=$(find . -name "maven-metadata-remote.xml" | sed 's@^.\/@@g')
		repository="http://nexus.gnp.mx/repository/maven-releases"
		groupid=$(xmllint --xpath "string(//metadata/groupId)" $maven_metadata_path | tr . /)
		artifactid=$(xmllint --xpath "string(//metadata/artifactId)" $maven_metadata_path)
		version=$(xmllint --xpath "string(//metadata/versioning/release)" $maven_metadata_path)

		version_full="$version"

		artifact_type="jar"
		artifact_fullname="$artifactid-$version.$artifact_type"
		artifact_url="$repository/$groupid/$artifactid/$version/$artifact_fullname"

		#Parametros de aplicacion de Automic
		application_name="$artifactid"
		custom_groupid="$groupid"
		custom_artifactid="$artifactid"
		custom_version="$version"
		custom_package="$artifact_type"
		custom_path="$groupid/$artifactid/$version/$artifact_fullname"
                custom_tag="$version"
	}
VersionType()
	{
                return 0
	}
CommitType()
        {
                return 0
        }
ArtifactStatus()
	{
                artifact_status=$(curl -s --head $artifact_url | head -n 1 | sed 's@^\(.*\)\r$@\1@g')
		if [ "$artifact_status" == "HTTP/1.1 200 OK" ]
			then
				return 0
			else
				PrintMsgTopic "Artefacto no disponible en Nexus:"
				PrintMsg "$artifact_url"
				return 1
		fi	
	}
ApplicationExist()
	{	
		artifact_id=$(curl -s --connect-timeout 60 -D- -u $login_data -X GET -H "Content-Type: application/json" http://"$automic_host":"$automic_port""$apiPath_applications"/\?name\=$application_name | grep -nB 1 "\"name\"\: \"$application_name\"" | grep -e "id" | sed 's@^.*\"id\"\: \(.*\),.*$@\1@g')

                if [ "$artifact_id" != "" ]
	                then
				return 0
			else
				PrintMsgTopic "La aplicación $artifact_name no existe en AUTOMIC"
				return 1
		fi
	}	
PackageCreate()
        {
                PrintMsgTopic "Creando paquete en Automic"
                curl -s --connect-timeout 60 -D- -u $login_data -X POST -H "Content-Type: application/json" http://"$automic_host":"$automic_port""$apiPath_packages" -d '{"name": "'$version_full'","application":{"name": "'$application_name'"},     "custom_type":{"name": "'$custom_type_name'"},"owner": {"name": "'$owner_name'"},"folder": {"name": "'$folder_name'"},"custom": {"groupid": "'$custom_groupid'", "artifactid": "'$custom_artifactid'", "version": "'$custom_version'", "package": "'$custom_package'", "path": "'$custom_path'", "tag": "'$custom_tag'"},}' > package_creation_out.out
                package_creation=$(cat package_creation_out.out | head -n 1 | sed 's@^\(.*\)\r$@\1@g')

                if [ "$package_creation" == "HTTP/1.1 201 Created" ]
                	then
                        	PrintMsg "El paquete $version_full ha sido creado en la aplicacion $application_name"
	                        return 0
                elif [ "$package_creation" = "HTTP/1.1 400 Bad Request" ]
        	        then
			        PrintMsg "$package_creation"
                        	package_creation=$(cat package_creation_out.out | grep -e "^.*\"error\": " | sed 's@^.*\"error\": \(.*\)\,\r$@\1@g')
	                        PrintMsg "El paquete no ha sido creado por el siguiente error:\n$package_creation\n"
        	                return 1
                fi
        }
PackageStatus()
        {
                package_id=$(cat package_creation_out.out | grep -B 3 -e "^.*\"owner\": {" | head -n 1 | sed 's@^  \"id\": \(.*\),.*$@\1@g')
		DeleteFile "package_creation_out.out"
                if [ "$package_id" != "" ]
	                then
        	                package_status=$(curl -s --connect-timeout 60 -D- -u $login_data -X GET -H "Content-Type: application/json" http://"$automic_host":"$automic_port""$apiPath_packages"/"$package_id" | head -n 1 | sed 's@^\(.*\)\r$@\1@g')
                	        if [ "$package_status" == "HTTP/1.1 200 OK" ]
                        		then
		                                return 0
                		        else
                                		PrintMsg "El paquete $package_id no esta disponible en automic"
		                                return 1
                	        fi
	                else
        	                PrintMsg "El paquete $package_id no esta disponible en automic"
                	        return 1
                fi
        }
PackageDeploy()
        {
                PrintMsgTopic "Desplegando paquete en Automic"
                execution_id=$(curl -s --connect-timeout 60 -D- -u $login_data -X POST -H "Content-Type: application/json" http://"$automic_host":"$automic_port""$apiPath_executions" -d '{"application":"'$application_name'","workflow":"'$workflow_name'","package":"'$version_full'","deployment_profile":"'$deployment_profile'","needs_manual_start":false,"install_mode":"OverwriteExisting"}' | grep -nA 3 "status" | tail -n 1 | sed 's@^.*\"id\"\: \(.*\).*\r$@\1@g')

                if [ "$execution_id" != "" ]
	                then
        	                execution_status=$(curl -s --connect-timeout 60 -D- -u $login_data -X GET -H "Content-Type: application/json" http://"$automic_host":"$automic_port""$apiPath_executions"/"$execution_id" | grep -e "status" | sed 's@^.*\"status\"\: \"\(.*\)\",.*\r$@\1@g')
                	        if [ "$execution_status" == "Active" ]
                        		then
		                                PrintMsg "Iniciando el despliegue del paquete $version_full de la aplicacion $application_name"
			                else
                                                PrintMsg "No se inicia el despliegue del paquete $version_full de la aplicacion $application_name"
	                        fi
                                end_flag="false"
                                while [ "$end_flag" == "false" ];do
                                        execution_status=$(curl -s --connect-timeout 60 -D- -u $login_data -X GET -H "Content-Type: application/json" http://"$automic_host":"$automic_port""$apiPath_executions"/"$execution_id" | grep -e "status" | sed 's@^.*\"status\"\: \"\(.*\)\",.*\r$@\1@g')
                                        if [ "$execution_status" == "Finished" ]
                                                then
                                                        PrintMsg "El despliegue del paquete $version_full de la aplicacion $application_name termino correctamente"
                                                        end_flag="true"
                                                        return 0
                                        elif [ "$execution_status" == "Canceled" ] || [ "$execution_status" == "Failed" ]
                                                then
                                                        PrintMsg "ERROR: El despliegue del paquete $version_full de la aplicacion $application_name se detuvo"
#                                                       reports=( $(curl -s --connect-timeout 60 -D- -u $login_data -X GET -H "Content-Type: application/json" http://"$automic_host":"$automic_port""$apiPath_executions"/"$execution_id"/report/content | grep -e "---------- Report ----------" -e "--------------------------------------------------------------------" -n | grep -A 1 -e "Report" | sed '$!N;s@^\(.*\):.*Report.*\n\(.*\):.*$@\1,\2@g') )
#                                                       for report in ${reports[*]};do
#                                                               echo -e "$report"
#                                                               curl -s --connect-timeout 60 -D- -u $login_data -X GET -H "Content-Type: application/json" http://"$automic_host":"$automic_port""$apiPath_executions"/"$execution_id"/report/content | sed -n "$report p"
#                                                       done
                                                        end_flag="true"
                                                        return 1
                                        fi
                                        sleep 5
                                done
        	        else
                                PrintMsg "No se inicia el despliegue del paquete $version_full de la aplicacion $application_name"
	                        return 1
                fi
        }
PrintMsg()
        {
                echo -e `date +"%Y-%m-%d %T.%4N"`" $1"
        }
PrintMsgTopic()
        {
                echo -e `date +"%Y-%m-%d %T.%4N"`"   --------------------------------------------------"
                echo -e `date +"%Y-%m-%d %T.%4N"`"  / $1"
                echo -e `date +"%Y-%m-%d %T.%4N"`" ----------------------------------------------------"
        }
DeleteFile()
        {
                if [ -f "$1" ]
        	        then
                	        rm -rf "$1"
                fi
        }
#Inicio
main