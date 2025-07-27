# Project Concepts Documentation: WatchWave-Like Video Platform

## Table of Contents
1. [Users (Object)](#users-object)
2. [Authentication & Authorization (Context)](#authentication--authorization-context)
3. [Videos (Object)](#videos-object)
4. [Video Upload & Streaming (Context)](#video-upload--streaming-context)
5. [Tags & Categories (Object)](#tags--categories-object)
6. [User Interactions (Context)](#user-interactions-context)
7. [Subscriptions (Object)](#subscriptions-object)
8. [Notifications (Context)](#notifications-context)
9. [Analytics & Reporting (Context)](#analytics--reporting-context)
10. [Security (Context)](#security-context)

## Users (Object)
- Unique id (UUID)
- Username (unique, display name)
- Email (unique, login)
- Password hash (BCrypt)
- Profile details: profile picture, gender, birthdate, location, bio
- Verification status, block status
- Timestamps: created_at, updated_at
- Roles assigned: USER, CREATOR, ADMIN
- User profile extended information stored separately

**Important Info:**
- User accounts uniquely identified via UUID
- Roles define system permissions and access
- Extended user info important for personalization and authorization

## Authentication & Authorization (Context)
- Uses JWT for stateless authentication
- Registration requires email verification token and password hashing
- Roles: USER (basic), CREATOR (upload videos), ADMIN (full access)
- Role-change requests handled with pending/approved/rejected statuses
- Secure password storage with BCrypt
- Endpoint protection with role-based access control using Spring Security

**Important Info:**
- JWT tokens expire (configurable)
- Admin approval workflow for sensitive role upgrades
- Secure routes require JWT tokens and correct roles

## Videos (Object)
- Unique id (UUID)
- Metadata: title, description, uploader_id, visibility (PUBLIC/PRIVATE/UNLISTED)
- Video file info: URL, file path, size, duration, resolution
- Thumbnail image info: URL, file path, size, MIME type
- Upload status (Uploading, Processing, Ready, Failed)
- Flags: is_comment_enabled, is_deleted (soft delete)
- View count, timestamps

**Important Info:**
- Videos linked to uploader (user)
- Soft delete allows for possible restoration by admins
- Multiple formats and size validation required

## Video Upload & Streaming (Context)
- Upload via multipart/form-data (video + optional thumbnail)
- File validation for type and size limits (e.g. 500MB for videos)
- Unique filenames generated to avoid conflicts
- Streaming supports HTTP Range requests for seeking & buffering
- Streaming endpoint delivers 206 Partial Content for optimal playback
- Public URLs abstract actual file storage locations

**Important Info:**
- Streaming optimizes bandwidth and supports large files
- Thumbnails improve user experience in listings
- Upload and streaming integrate tightly with authorization checks

## Tags & Categories (Object)
- Tags have unique lowercase names
- Many-to-many relationship with videos (video_tags table)
- Tags created automatically on first use
- Used for video categorization and searchability

**Important Info:**
- Reuse tags across multiple videos for consistency
- Tag management is lightweight but crucial for user discovery

## User Interactions (Context)
- Likes and dislikes per video (one per user)
- Threaded comments with soft delete and moderation
- Watch Later list for bookmarking videos
- Comments support parent-child relationships for replies
- Interaction counts reflected in real-time analytics

**Important Info:**
- Only authenticated users can interact (like/comment/watch later)
- Soft delete preserves thread continuity
- Ownership and role-based moderation enforced

## Subscriptions (Object)
- Users subscribe to creators (with CREATOR role)
- Unique subscriber-creator pairs
- Subscription lists accessible by subscriber and creators
- Self-subscription forbidden

**Important Info:**
- Subscription enhances user engagement and notifications
- Role enforcement important to prevent invalid subscriptions

## Notifications (Context)
- Notifications have user_id, message, type, redirect URL
- Read/unread status managed
- Real-time or batch delivery to users for engagement events
- Examples: new video from subscribed creator, role change approved

**Important Info:**
- Notifications keep users informed and engaged
- System needs to scale as user base grows

## Analytics & Reporting (Context)
- View history tracks user video watch duration, progress
- Aggregated video statistics: views, likes, dislikes, avg watch time
- Reports for content moderation with resolution status
- Data used for recommendations, trending, and admin decisions

**Important Info:**
- Data integrity critical for accurate analytics
- Reporting enables platform safety and quality

## Security (Context)
- Passwords hashed using BCrypt with salt
- JWT tokens signed with secret key, expire after specific duration
- Authorization with Spring Security annotations
- Input validation and safe query practices prevent injection attacks
- CORS policies configured for frontend-backend separation

**Important Info:**
- Layered security approach
- Role-based access minimizes risk exposure

