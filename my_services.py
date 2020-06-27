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
        postgreSQL_select_Query = "Select beauty_service_item_id from beautician_services where beautician_id=%s"
        
        cursor.execute(postgreSQL_select_Query, (id,))
        beautician_records = cursor.fetchall()
        
        body = defaultdict(list)

        for row in beautician_records:
            item = row[0]
          
            postgreSQL_select_Query = "Select name from beauty_service_items where id=%s"
            cursor.execute(postgreSQL_select_Query,(row[0],))
            beauty_services = cursor.fetchall()
            for service in beauty_services:
                body["name"].append(service)
            
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

    
   