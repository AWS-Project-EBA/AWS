import json
import sys
import logging
import psycopg2
import datetime
from collections import defaultdict
import dateutil.tz as t

utc=t.gettz()

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
        postgreSQL_select_Query = "Select appointment_id,appointment_date,user_id,user_address_id from payments where beautician_id=%s"
        
        cursor.execute(postgreSQL_select_Query, (id,))
        appointment_records = cursor.fetchall()
        
        body = defaultdict(list)

        for row in appointment_records:
            date = row[1]
            if(date.replace(tzinfo=utc) > datetime.datetime.now().replace(tzinfo=utc)):
                body["date"].append(row[1].date().strftime("%Y-%m-%d"))
                
                postgreSQL_select_Query = "Select time from appointments where id=%s"
                cursor.execute(postgreSQL_select_Query,(row[0],))
                appointment_hours = cursor.fetchone()
                body["hour"].append(appointment_hours[0])
                
                postgreSQL_select_Query = "Select first_name,last_name from users where id=%s"
                cursor.execute(postgreSQL_select_Query,(row[2],))
                customer_info = cursor.fetchone()
                body["customer"].append(str(customer_info[0]) + " " + str(customer_info[1]))
                
                postgreSQL_select_Query = "Select address,district,neighborhood,building_no,door_no,city from user_addresses where id=%s"
                cursor.execute(postgreSQL_select_Query,(row[3],))
                address_info = cursor.fetchone()
                address = address_info[0] + "," + address_info[1] + "," +address_info[2] + ",apartman no: "+ address_info[3] + " daire no: "+ address_info[4] + "/"+ address_info[5]
                body["address"].append(address)
                
            
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
    
