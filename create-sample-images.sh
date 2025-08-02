#!/bin/bash

# Script to create sample placeholder images for the seeder
# This script creates simple colored rectangles as placeholder images

SEEDER_IMAGES_DIR="src/main/resources/seeder-images"

# Create the directory if it doesn't exist
mkdir -p "$SEEDER_IMAGES_DIR"

echo "Creating sample placeholder images for the seeder..."

# Check if ImageMagick is available
if command -v convert &> /dev/null; then
    echo "Using ImageMagick to create images..."
    
    # Course images (800x600)
    convert -size 800x600 xc:lightblue -pointsize 40 -gravity center -annotate 0 "Course 1" "$SEEDER_IMAGES_DIR/course1.jpg"
    convert -size 800x600 xc:lightgreen -pointsize 40 -gravity center -annotate 0 "Course 2" "$SEEDER_IMAGES_DIR/course2.jpg"
    convert -size 800x600 xc:lightcoral -pointsize 40 -gravity center -annotate 0 "Course 3" "$SEEDER_IMAGES_DIR/course3.jpg"
    convert -size 800x600 xc:lightyellow -pointsize 40 -gravity center -annotate 0 "Course 4" "$SEEDER_IMAGES_DIR/course4.jpg"
    convert -size 800x600 xc:lightpink -pointsize 40 -gravity center -annotate 0 "Course 5" "$SEEDER_IMAGES_DIR/course5.jpg"
    
    # Profile images (400x400)
    convert -size 400x400 xc:lightblue -pointsize 30 -gravity center -annotate 0 "Profile 1" "$SEEDER_IMAGES_DIR/profile1.jpg"
    convert -size 400x400 xc:lightgreen -pointsize 30 -gravity center -annotate 0 "Profile 2" "$SEEDER_IMAGES_DIR/profile2.jpg"
    convert -size 400x400 xc:lightcoral -pointsize 30 -gravity center -annotate 0 "Profile 3" "$SEEDER_IMAGES_DIR/profile3.jpg"
    convert -size 400x400 xc:lightyellow -pointsize 30 -gravity center -annotate 0 "Profile 4" "$SEEDER_IMAGES_DIR/profile4.jpg"
    
    # Learning path images (800x600)
    convert -size 800x600 xc:plum -pointsize 40 -gravity center -annotate 0 "Learning Path 1" "$SEEDER_IMAGES_DIR/learning1.jpg"
    convert -size 800x600 xc:orange -pointsize 40 -gravity center -annotate 0 "Learning Path 2" "$SEEDER_IMAGES_DIR/learning2.jpg"
    
elif command -v ffmpeg &> /dev/null; then
    echo "Using FFmpeg to create images..."
    
    # Course images
    ffmpeg -f lavfi -i color=c=lightblue:size=800x600 -vf "drawtext=text='Course 1':fontsize=40:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/course1.jpg" -y
    ffmpeg -f lavfi -i color=c=lightgreen:size=800x600 -vf "drawtext=text='Course 2':fontsize=40:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/course2.jpg" -y
    ffmpeg -f lavfi -i color=c=lightcoral:size=800x600 -vf "drawtext=text='Course 3':fontsize=40:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/course3.jpg" -y
    ffmpeg -f lavfi -i color=c=lightyellow:size=800x600 -vf "drawtext=text='Course 4':fontsize=40:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/course4.jpg" -y
    ffmpeg -f lavfi -i color=c=lightpink:size=800x600 -vf "drawtext=text='Course 5':fontsize=40:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/course5.jpg" -y
    
    # Profile images
    ffmpeg -f lavfi -i color=c=lightblue:size=400x400 -vf "drawtext=text='Profile 1':fontsize=30:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/profile1.jpg" -y
    ffmpeg -f lavfi -i color=c=lightgreen:size=400x400 -vf "drawtext=text='Profile 2':fontsize=30:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/profile2.jpg" -y
    ffmpeg -f lavfi -i color=c=lightcoral:size=400x400 -vf "drawtext=text='Profile 3':fontsize=30:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/profile3.jpg" -y
    ffmpeg -f lavfi -i color=c=lightyellow:size=400x400 -vf "drawtext=text='Profile 4':fontsize=30:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/profile4.jpg" -y
    
    # Learning path images
    ffmpeg -f lavfi -i color=c=plum:size=800x600 -vf "drawtext=text='Learning Path 1':fontsize=40:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/learning1.jpg" -y
    ffmpeg -f lavfi -i color=c=orange:size=800x600 -vf "drawtext=text='Learning Path 2':fontsize=40:fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2" -frames:v 1 "$SEEDER_IMAGES_DIR/learning2.jpg" -y
    
else
    echo "Neither ImageMagick nor FFmpeg found. Creating simple text files as placeholders..."
    
    # Create simple text files as placeholders
    echo "Course 1 Placeholder Image" > "$SEEDER_IMAGES_DIR/course1.jpg"
    echo "Course 2 Placeholder Image" > "$SEEDER_IMAGES_DIR/course2.jpg"
    echo "Course 3 Placeholder Image" > "$SEEDER_IMAGES_DIR/course3.jpg"
    echo "Course 4 Placeholder Image" > "$SEEDER_IMAGES_DIR/course4.jpg"
    echo "Course 5 Placeholder Image" > "$SEEDER_IMAGES_DIR/course5.jpg"
    
    echo "Profile 1 Placeholder Image" > "$SEEDER_IMAGES_DIR/profile1.jpg"
    echo "Profile 2 Placeholder Image" > "$SEEDER_IMAGES_DIR/profile2.jpg"
    echo "Profile 3 Placeholder Image" > "$SEEDER_IMAGES_DIR/profile3.jpg"
    echo "Profile 4 Placeholder Image" > "$SEEDER_IMAGES_DIR/profile4.jpg"
    
    echo "Learning Path 1 Placeholder Image" > "$SEEDER_IMAGES_DIR/learning1.jpg"
    echo "Learning Path 2 Placeholder Image" > "$SEEDER_IMAGES_DIR/learning2.jpg"
    
    echo "Note: These are text files, not actual images. Install ImageMagick or FFmpeg for proper image generation."
fi

echo "Sample images created successfully!"
echo "You can now run the database seeder to use these images."
echo "Images are located in: $SEEDER_IMAGES_DIR" 