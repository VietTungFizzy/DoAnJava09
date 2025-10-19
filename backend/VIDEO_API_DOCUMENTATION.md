# Video Management API Documentation

## Overview
This API provides comprehensive video management functionality including upload, retrieval, search, categorization, and statistics.

## Base URL
```
http://localhost:8081/api/videos
```

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### 1. Create Video
**POST** `/api/videos`

Creates a new video entry.

**Authentication:** Required (USER or ADMIN)

**Request Body:**
```json
{
  "title": "Video Title",
  "description": "Video description",
  "videoUrl": "https://example.com/video.mp4",
  "thumbnailUrl": "https://example.com/thumbnail.jpg",
  "duration": 1800,
  "fileSize": 157286400,
  "videoFormat": "mp4",
  "resolution": "1080p",
  "category": "Education",
  "tags": "java,programming,tutorial",
  "isFeatured": false,
  "isPublic": true,
  "status": "ACTIVE"
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Video Title",
  "description": "Video description",
  "videoUrl": "https://example.com/video.mp4",
  "thumbnailUrl": "https://example.com/thumbnail.jpg",
  "duration": 1800,
  "formattedDuration": "30:00",
  "fileSize": 157286400,
  "formattedFileSize": "150.0 MB",
  "videoFormat": "mp4",
  "resolution": "1080p",
  "category": "Education",
  "tags": ["java", "programming", "tutorial"],
  "viewCount": 0,
  "likeCount": 0,
  "status": "ACTIVE",
  "isFeatured": false,
  "isPublic": true,
  "uploadedByName": "John Doe",
  "uploadedById": 1,
  "createdAt": "2023-12-01T10:00:00",
  "updatedAt": "2023-12-01T10:00:00"
}
```

### 2. Get Video by ID
**GET** `/api/videos/{id}`

Retrieves a specific video by ID.

**Authentication:** Not required for public videos

**Response:** Same as Create Video response

### 3. View Video (Increment View Count)
**POST** `/api/videos/{id}/view`

Retrieves a video and increments its view count.

**Authentication:** Not required

**Response:** Same as Get Video response

### 4. Update Video
**PUT** `/api/videos/{id}`

Updates an existing video. Only the video owner or admin can update.

**Authentication:** Required (USER or ADMIN)

**Request Body:**
```json
{
  "title": "Updated Title",
  "description": "Updated description",
  "thumbnailUrl": "https://example.com/new-thumbnail.jpg",
  "category": "Technology",
  "tags": "java,spring,advanced",
  "isFeatured": true,
  "isPublic": true,
  "status": "ACTIVE"
}
```

**Response:** Updated video object

### 5. Delete Video
**DELETE** `/api/videos/{id}`

Soft deletes a video (sets status to DELETED). Only the video owner or admin can delete.

**Authentication:** Required (USER or ADMIN)

**Response:**
```json
{
  "success": true,
  "message": "Video deleted successfully",
  "timestamp": 1701432000000
}
```

### 6. Get All Videos
**GET** `/api/videos`

Retrieves all active videos with pagination and sorting.

**Query Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size
- `sortBy` (default: createdAt) - Sort field
- `sortDir` (default: desc) - Sort direction (asc/desc)

**Response:**
```json
{
  "content": [...videos...],
  "pageable": {...},
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "first": true,
  "numberOfElements": 10
}
```

### 7. Get Public Videos
**GET** `/api/videos/public`

Retrieves only public videos with pagination.

**Query Parameters:** Same as Get All Videos

### 8. Get Featured Videos
**GET** `/api/videos/featured`

Retrieves all featured videos.

**Response:** Array of video objects

### 9. Get Videos by Category
**GET** `/api/videos/category/{category}`

Retrieves videos in a specific category.

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

### 10. Get Videos by User
**GET** `/api/videos/user/{userId}`

Retrieves videos uploaded by a specific user.

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

### 11. Get Current User's Videos
**GET** `/api/videos/my-videos`

Retrieves videos uploaded by the authenticated user.

**Authentication:** Required (USER or ADMIN)

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

### 12. Search Videos
**GET** `/api/videos/search`

Searches videos by title or description.

**Query Parameters:**
- `keyword` (required) - Search keyword
- `page` (default: 0)
- `size` (default: 10)

### 13. Get Videos by Tag
**GET** `/api/videos/tag/{tag}`

Retrieves videos containing a specific tag.

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

### 14. Get Most Viewed Videos
**GET** `/api/videos/most-viewed`

Retrieves videos sorted by view count (descending).

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

### 15. Get Most Liked Videos
**GET** `/api/videos/most-liked`

Retrieves videos sorted by like count (descending).

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

### 16. Get Recent Videos
**GET** `/api/videos/recent`

Retrieves recently uploaded videos.

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)

### 17. Like Video
**POST** `/api/videos/{id}/like`

Increments the like count of a video.

**Authentication:** Required (USER or ADMIN)

**Response:**
```json
{
  "success": true,
  "message": "Video liked successfully",
  "timestamp": 1701432000000
}
```

### 18. Unlike Video
**POST** `/api/videos/{id}/unlike`

Decrements the like count of a video.

**Authentication:** Required (USER or ADMIN)

### 19. Get All Categories
**GET** `/api/videos/categories`

Retrieves all distinct video categories.

**Response:**
```json
["Education", "Technology", "Business", "Entertainment"]
```

### 20. Get Video Statistics
**GET** `/api/videos/statistics`

Retrieves video statistics (admin only).

**Authentication:** Required (ADMIN)

**Response:**
```json
{
  "totalVideos": 150,
  "totalViews": 50000,
  "averageViews": 333.33
}
```

## Error Responses

All endpoints return error responses in the following format:

```json
{
  "success": false,
  "message": "Error description",
  "timestamp": 1701432000000
}
```

## HTTP Status Codes

- `200 OK` - Success
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Access denied
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Data Validation

### Video Request Validation:
- `title`: Required, max 255 characters
- `description`: Optional, max 1000 characters
- `videoUrl`: Required, valid URL
- `videoFormat`: Optional, max 50 characters
- `resolution`: Optional, max 20 characters
- `category`: Optional, max 100 characters

### Video Update Request Validation:
- All fields are optional
- Same length restrictions apply when provided

## Usage Examples

### Create a Video
```bash
curl -X POST http://localhost:8081/api/videos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "My Video",
    "description": "Video description",
    "videoUrl": "https://example.com/video.mp4",
    "category": "Education",
    "tags": "tutorial,learning"
  }'
```

### Search Videos
```bash
curl "http://localhost:8081/api/videos/search?keyword=java&page=0&size=5"
```

### Get Featured Videos
```bash
curl "http://localhost:8081/api/videos/featured"
```

This API provides comprehensive video management capabilities suitable for video sharing platforms, educational systems, or content management applications.
