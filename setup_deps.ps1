Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
Remove-Item -Path "C:\Users\TRAM\.m2\repository" -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Wiping done. Starting offline downloads sequentially to prevent file locking..."

cd product-service
.\mvnw.cmd dependency:go-offline
cd ..

cd inventory-service
.\mvnw.cmd dependency:go-offline
cd ..

cd order-service
.\mvnw.cmd dependency:go-offline
cd ..

cd api-gateway
.\mvnw.cmd dependency:go-offline
cd ..

Write-Host "All dependencies downloaded successfully!"
