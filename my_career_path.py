import json
import sys
import logging
import psycopg2

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
        postgreSQL_select_Query = "Select beautician_id, count(*) from payments where beautician_id=%s group by beautician_id"
        
        cursor.execute(postgreSQL_select_Query, (id,))
        payments_records = cursor.fetchall() 
        
        for row in payments_records:
            print(row)
    
        level = 4    
        if(payments_records[0][1] <= 10):
            level = 1
            required_next_level = 11-payments_records[0][1]
        elif(payments_records[0][1] > 10 and payments_records[0][1] <= 25):
            level = 2
            required_next_level = 25- payments_records[0][1]
        elif(payments_records[0][1] > 25 and payments_records[0][1] < 50):
            level = 3
            required_next_level = 50 - payments_records[0][1]

        if(connection):
            cursor.close()
            connection.close()
        
        body = {"beautician_id": row[0],"count": row[1],"level": level,"next_level": required_next_level}
        
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

    
   

