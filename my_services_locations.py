import json
import sys
import logging
import psycopg2
import datetime
from collections import defaultdict

##Enter database credentials
db_username = " "
db_password = " "
db_name = " "
            
def lambda_handler(event, context):
    
    #rds settings
    rds_host  = "beautify.ckihqi81vej1.eu-central-1.rds.amazonaws.com"
    name = db_username
    password = db_password
    
    id = event['queryStringParameters']['id']

    try:
        connection = psycopg2.connect(user = name,password = password,host = rds_host,port = "5432",database = db_name)
    
        cursor = connection.cursor()
        postgreSQL_select_Query = "Select country_id,city_id,district_id from beautician_locations where beautician_id=%s"
        
        cursor.execute(postgreSQL_select_Query, (id,))
        beautician_records = cursor.fetchall()
        
        body = defaultdict(list)

        for row in beautician_records:
            country = row[0]
            city = row[1]
            district = row[2]
            
            postgreSQL_select_Query = "Select city_name,city_key from cities where id=%s"
            cursor.execute(postgreSQL_select_Query,(row[1],))
            city = cursor.fetchone()
            key = city[1]
            city_name = city[0]
            
            postgreSQL_select_Query = "Select country_name from countries where id=%s"
            cursor.execute(postgreSQL_select_Query,(row[1],))
            country = cursor.fetchone()

            postgreSQL_select_Query = "Select district_name from districts where id=%s AND city_key = %s"
            cursor.execute(postgreSQL_select_Query,(row[2],key,))
            district = cursor.fetchone()
            
            location =   district[0] + " " +  city_name +  " " + country[0] 

            body['location'].append(location)
    
        response = {
            "statusCode": 200,
            "headers": {
                "content-type": "application/json"
            },
            "body": json.dumps(body,ensure_ascii=False),
            "isBase64Encoded": False,
        }
        
    except (Exception, psycopg2.Error) as error :
        body = {'error': error}
        response = {
            "statusCode": 400,
            "headers": {
                "content-type": "application/json"
            },
            "body": json.dumps(body),
            "isBase64Encoded": False,
        }
        
    return response

    
   
