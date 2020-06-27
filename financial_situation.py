import json
import sys
import logging
import psycopg2
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
        current_time = datetime.datetime.now()  
        month = current_time.month
        year = current_time.year
        
        postgreSQL_select_Query = "SELECT SUM(purch_amount) from payments where beautician_id=%s AND EXTRACT(MONTH FROM created_at)=%s AND EXTRACT(YEAR FROM created_at)=%s"
        cursor.execute(postgreSQL_select_Query, (id,month,year,))
        monthly_payments_records = cursor.fetchall() 
        postgreSQL_select_Query = "SELECT SUM(purch_amount) from payments where beautician_id=%s AND EXTRACT(YEAR FROM created_at)=%s"
        cursor.execute(postgreSQL_select_Query, (id,year,))
        yearly_payments_records = cursor.fetchall() 
        

        if(connection):
            cursor.close()
            connection.close()
        
        body ={"yearly_financial": yearly_payments_records[0][0], "monthly_financial": monthly_payments_records[0][0]}
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

    
   

