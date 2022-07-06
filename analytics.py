# import requests
# import json
# response = requests.get("https://resqme-60664-default-rtdb.firebaseio.com/LOG.json")
# print(response.status_code)
# print(response.url)
# log_data_json = response.json()

# # convert log_data_json to a list 
# log_data_list = []
# for key, value in log_data_json.items():
#     log_data_list.append(value)

# for item in log_data_list:
#     print(item)

# print(len(log_data_list))


# Read local json file
import json
import os


def read_json_file(file_name):
    with open(file_name, 'r', encoding="utf8") as f:
        data = json.load(f)
    return data

data = read_json_file('resqme-60664-default-rtdb-export (1).json')

# Convert data to list of dictionaries
data_list = []
for key, value in data.items():
    data_list.append(value)

print(data_list)