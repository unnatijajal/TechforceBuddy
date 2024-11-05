from py_eureka_client import eureka_client

# Define the URL of your Eureka server and the details for your service
eureka_server = "http://localhost:8761"  # Replace with your Eureka server URL
app_name = "python-service"  # Name of the service as it will appear in Eureka
instance_port = 5000  # The port your Python service is running on

# Initialize the Eureka client to register your Python service
eureka_client.init(
    eureka_server=eureka_server,
    app_name=app_name,
    instance_port=instance_port,
    instance_ip="192.168.1.214",  # IP address for the instance
   
)
