# pistachio

## This service is my business be application

```
curl -X POST http://localhost:1010/pistachio/business/businesses/office/listing/create \
  -H "Content-Type: application/json" \
  -d '{
    "officeId": "office_id_1",
    "officeSpecs": {
      "id": 4,
      "businessId": "business_id_1",
      "officeId": "office_id_1",
      "officeName": "Modern Workspace",
      "description": "A vibrant office space in the heart of the city, ideal for teams or individuals.",
      "officeType": "OpenPlanOffice",
      "numberOfFloors": 3,
      "totalDesks": 3,
      "capacity": 50,
      "availability": {
        "days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
        "startTime": "2024-11-21T10:00:00",
        "endTime": "2024-11-21T10:30:00"
      },
      "amenities": ["Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"],
      "rules": "No smoking. Maintain cleanliness.",
      "createdAt": "2025-01-01T00:00:00",
      "updatedAt": "2025-01-01T00:00:00"
    },
    "addressDetails": {
      "id": 4,
      "businessId": "business_id_1",
      "officeId": "office_id_1",
      "buildingName": "build_123",
      "floorNumber": "floor 1",
      "street": "123 Main Street",
      "city": "New York",
      "country": "USA",
      "county": "New York County",
      "postcode": "10001",
      "latitude": 100.1,
      "longitude": -100.1,
      "createdAt": "2025-01-01T00:00:00",
      "updatedAt": "2025-01-01T00:00:00"
    },
    "contactDetails": {
      "id": 4,
      "businessId": "business_id_1",
      "officeId": "office_id_1",
      "primaryContactFirstName": "Michael",
      "primaryContactLastName": "Yau",
      "contactEmail": "mike@gmail.com",
      "contactNumber": "07402205071",
      "createdAt": "2025-01-01T00:00:00",
      "updatedAt": "2025-01-01T00:00:00"
    },
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-01-01T00:00:00"
  }'

```

```
http POST http://localhost:1010/pistachio/business/desk/listing/create Content-Type:application/json \
business_id="business_1" workspace_id="workspace_123" title="Executive Desk" \
description="A premium desk in the executive lounge with all amenities." desk_type="PrivateDesk" \
quantity:=5 price_per_hour:=10.50 price_per_day:=75.00 \
rules="No food or drink allowed on the desk." \
features:='["Wi-Fi", "Adjustable Height", "Monitor", "Power Outlets"]' \
availability:='{"days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"], "startTime": "09:00:00", "endTime": "18:00:00"}' \
created_at="2024-11-30T03:25:18" updated_at="2024-11-30T03:25:18"



```
