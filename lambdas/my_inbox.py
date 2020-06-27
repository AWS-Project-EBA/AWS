import json
import sys
import logging
import psycopg2
from collections import defaultdict
import datetime

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
        postgreSQL_select_Query = "Select title,body,created_at from notifications where user_id=%s"
        
        cursor.execute(postgreSQL_select_Query, (id,))
        inbox_records = cursor.fetchall()
    
        if(connection):
            cursor.close()
            connection.close()
        
        body = defaultdict(list)
        
        for row in inbox_records:
            body['title'].append(row[0])
            body['body'].append(row[1])
            body['date'].append(row[2].strftime("%Y-%m-%d, %H:%M:%S"))
        
        response = {
            "statusCode": 200,
            "headers": {
                "content-type": "application/json"
            },
            "body": json.dumps(body),
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

    
   

