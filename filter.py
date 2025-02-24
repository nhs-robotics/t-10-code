import cv2
import numpy as np
import time

def filter_color(image, lower, upper, is_red=False):
    """Optimized color filtering function"""
    # Pre-compute HSV conversion once

    global hsv_image

    if is_red:
        # Special handling for red since it wraps around
        mask1 = cv2.inRange(hsv_image, lower[0], upper[0])
        mask2 = cv2.inRange(hsv_image, lower[1], upper[1])
        mask = cv2.bitwise_or(mask1, mask2)
    else:
        mask = cv2.inRange(hsv_image, lower, upper)
    
    # Use faster bitwise_and with pre-allocated array
    image = cv2.bitwise_and(image, image, mask=mask)
    canny = cv2.Canny(image, 0, 100, apertureSize=5)
    contours, _ = cv2.findContours(canny, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

    if not contours:
        return image

    
    contours = list(contours)
    
    idx = 0
    rect = cv2.boundingRect(contours[idx])
    most_area = rect[2] * rect[3]

    for i, con in enumerate(contours):
        rect = cv2.boundingRect(con)
        area = rect[2] * rect[3]

        if area > most_area:
            idx = i
            most_area = area
    

    rect = cv2.boundingRect(contours[idx])
    image = cv2.rectangle(image, rect, (255, 255, 0))

    estimated_distance = (1.5 * 649.5832) / rect[2]
    print(estimated_distance, 'in')

    return mask

# Read the image once
image = cv2.imread('image.png')

angle = 4.9
image_center = tuple(np.array(image.shape[1::-1]) / 2)
rot_mat = cv2.getRotationMatrix2D(image_center, angle, 1.0)
image = cv2.warpAffine(image, rot_mat, image.shape[1::-1], flags=cv2.INTER_LINEAR)

# Pre-compute HSV conversion (used multiple times)
hsv_image = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

# Define color ranges (as before)
yellow_lower = np.array([20, 100, 70])
yellow_upper = np.array([32, 255, 255])

blue_lower = np.array([90, 50, 50])
blue_upper = np.array([130, 255, 255])

# Pack red ranges together for easier handling
red_lower = (np.array([0, 50, 100]), np.array([170, 50, 100]))
red_upper = (np.array([10, 255, 255]), np.array([180, 255, 255]))

# Process all colors
results = {
    'yellow': filter_color(image, yellow_lower, yellow_upper),
    'blue': filter_color(image, blue_lower, blue_upper),
    'red': filter_color(image, red_lower, red_upper, is_red=True)
}

# Batch save results
for color, result in results.items():
    cv2.imwrite(f'{color}_objects.jpg', result)
