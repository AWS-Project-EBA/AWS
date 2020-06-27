import json
import sys
import logging
import psycopg2
import boto3

##Enter database credentials
db_username = " "
db_password = " "
db_name = " "

def lambda_handler(event, context):
    
    rds_host  = "beautify.ckihqi81vej1.eu-central-1.rds.amazonaws.com"
    name = db_username
    password = db_password
    
    email = event['queryStringParameters']['email']

    try:
        connection = psycopg2.connect(user = name,password = password,host = rds_host,port = "5432",database = db_name)
    
        cursor = connection.cursor()
        postgreSQL_select_Query = "Select id from users where email=%s"
        
        cursor.execute(postgreSQL_select_Query, (email,))
        user_records = cursor.fetchall() 

        if(connection):
            cursor.close()
            connection.close()
        
        #subscription(user_records[0][0])
        
        body = {"id": user_records[0][0]}
        
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
"""
def subscription(id):
    
    client = boto3.client('sns')
    rds_host  = "beautify.ckihqi81vej1.eu-central-1.rds.amazonaws.com"
    name = db_username
    password = db_password
    
    connection = psycopg2.connect(user = name,password = password,host = rds_host,port = "5432",database = db_name)
    
    cursor = connection.cursor()
    postgreSQL_select_Query = "Select phone_number, email from users where id=%s"
       
    cursor.execute(postgreSQL_select_Query, (id,))
    user_info = cursor.fetchone() 

    response = client.subscribe(
        TopicArn='arn:aws:sns:eu-central-1:935545223210:notifications',
        Protocol='email',
        Endpoint=user_info[1],
        Attributes={},
        ReturnSubscriptionArn=True
    )

        response = client.subscribe(
            TopicArn='arn:aws:sns:eu-central-1:935545223210:notifications',
            Protocol='sms',
            Endpoint=user_info[0],
            Attributes={},
            ReturnSubscriptionArn=True
        )

    except:
        pass
"""
