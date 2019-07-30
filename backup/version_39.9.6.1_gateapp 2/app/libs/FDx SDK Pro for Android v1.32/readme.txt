SecuGen FDx SDK PRO for Android 
Version 1.32
Date: January 29, 2018
#################################################################

================================================================ 
DISCLAIMER
================================================================= 
This SDK is provided “as is” and without warranty of any kind. 
The SDK may contain errors that could cause failures or loss of 
data, and may be incomplete or contain inaccuracies. By using this 
SDK, you expressly acknowledge and agree that use of the SDK is 
at your risk. 

================================================================ 
Release Notes:
================================================================= 
1. This SDK requires Android 3.1 or later (Honeycomb API Level 12)
2. This version supports the following SecuGen devices:
     USB Hamster PRO 10 VID:0x1162 PID:0x2203 (U10 class device)
     USB Hamster PRO VID:0x1162 PID:0x2201 (UPx class device)
     USB Hamster PRO 20 VID:0x1162 PID:0x2200 (U20 class device)
     USB Hamster IV VID:0x1162 PID:0x2000 (SDU04P class device with WHITE LEDs)
     USB Hamster Plus VID:0x1162 PID:0x1000 (SDU03P class device)
3. This version has been tested with the following Android configurations:
     Toshiba Thrive Tablet - Ice Cream Sandwich 4.0.4
     DoublePower 7" tablet - Jelly Bean 4.1.1
     Huawei Honor T1-701U - KitKat 4.4.2
     Lenovo Tab A10 A7600-F - KitKat 4.4.2
     DELL Venue 10 5050 - Lollipop 5.0.2 (BULK MODE 64 FOR SDU03P)
     Samsung Galaxy Tab S2 SM-T710 - Lollipop 5.1.1 (BULK MODE 64 FOR SDU03P)
     Samsung Galaxy Tab S2 SM-T710 - Marshmallow 6.0.1 (BULK MODE 64 FOR SDU03P)
     Samsung Galaxy Tab A SM-T550 - Marshmallow 6.0.1
     Samsung Galaxy Tab A with S Pen SM-P580 - Marshmallow 6.0.1
     LG Aristo Phone 5" - Nougat 7.0
     Motorola G5 Plus - Nougat 7.0
     Lenovo Tab 4 10 - Nougat 7.1.1 (64bit)
4. Earlier versions of this software have been tested with the following Android configurations:
     Toshiba Thrive Tablet - Honeycomb 3.2.1
     Samsung Galaxy S3 - Ice Cream Sandwich 4.0.3
     Samsung Galaxy Note - Ice Cream Sandwich 4.0.4 (CTB1)
     Motorola XYBoard Tablet - Ice Cream Sandwich 4.0.4 (CTB1)
     Viewsonic VSD 220 - Ice Cream Sandwich 4.0.4 (CTB1)
     Nexus 10 - Jelly Bean 4.1 (CT)
     Samsung Galaxy Note 3 - Jelly Bean 4.1 (CT)
     Samsung Galaxy S3 - Jelly Bean 4.1.1
     Samsung Galaxy Nexus - Jelly Bean 4.1.1
     Samsung Galaxy Tablet P5100 - Jelly Bean 4.1.1 (CTB1)
     Azpen A700 7" Tablet - Jelly Bean 4.2.2
     Samsung Galaxy Note 3 - Jelly Bean 4.3
     ASUS Transformer Prime -  Jelly Bean 4.3 (CT)
     Samsung Galaxy S4 - KitKat 4.4.2 (CT)
     Samsung Galaxy S5 - KitKat 4.4.2
     Intel NUC DN2820FYKH - KitKat 4.4.2
     LG G2 (LG-D800) - KitKat 4.4.2
     Samsung Galaxy Note 10.1 - KitKat 4.4.2
     Samsung Galaxy S5 - Lollipop 5.0
     Samsung Galaxy Note 4 - Lollipop 5.0.1
     Google Note 7 - Lollipop 5.0.2
     Samsung Galaxy S5 - Marshmallow 6.0.1
     Samsung Note 5 - Marshmallow 6.0.1
     Google Nexus 9 - Marshmallow 6.0.1
5. There may be some functionality issues on the following devices:
     Samsung Galaxy Tab 3 8" (SM-T310) - Jelly Bean 4.2.2 (Low power on USB port)
6. The following devices are not supported:
     Samsung Galaxy S2 - Jelly Bean 4.1.1 
     Samsung Galaxy Tab 3 7" (SM-T210) - Jelly Bean 4.1.2 (No USB OTG Support))
     ASUS Memo Pad HD7 (ME173X) - Jelly Bean 4.2.1 (No USB OTG Support))    
7. This version supports armeabi and x86 Android targets

================================================================= 
SYSTEM INSTALLATION NOTES
================================================================= 
1. Verify Android Host Compatibility
1.1 Android OS Version should be 3.1 or later
1.2 Follow the instructions in "Android_USB_Host_Diagnostics.pdf"
    to verify that your Android device has a functional USB
    host controller.
2. Demo Application
2.1 Copy SecuGenUSB.apk to your Android device and install it
2.2 Plug in SecuGen SDU03P, SDU04P or U20 device.
2.3 Launch SecuGen.Demo.JSGDActivity.
3. Build environment
3.1 The Following tools were used
    Eclipse Helios Service Release 2
    Java JDK 1.6.0_24
    Android SDK for Windows (installer_r20.0.3-windows)
    Android USB ADB Driver (adb_driver_x86XP_Eng_Multi)
3.2 Import the "SecuGenUSBDist" project into your Eclipse environment
3.3 Select project properties and add external jar "FDxSDKProAndroid.jar"
    to the "Java build Path" section.

================================================================= 
REVISIONS
================================================================= 
1.32 1/29/2019 - Fixed slow initialization problem with Hamster PRO 10.
1.31 7/20/2018 - Fixed Crashing problem in 64bit ABI.
		 Implemented the following functions:
			MatchTemplateEx()
			GetMatchingScoreEx()
1.30 6/4/2018  - Added support for compact ISO templates.
                 Modified sample application to extract ISO templates.
1.29 4/4/2018  - Doc update.
1.28 3/28/2018 - Added support for Hamster PRO 10.
1.24 11/28/2017 - Added 64bit native libraries. Tested on 32bit only
                 Built and tested with Hamster PRO 20, Hamster PRO
                 Hamster IV and Hamster Plus
1.19 4/28/2017 - Fixed problem with NULL ISO templates
1.14 2/18/2017 - Added code for firmware update
1.13 11/22/2016- Added 64byte USB packet mode to clear horizontal line images
                 with Hamster Plus (SDU03P) on some devices. This mode enabled by 
                 calling WriteData(Constant.WRITEDATA_COMMAND_ENABLE_USB_MODE_64, 0x01)
1.12 11/02/2016- Remove libpng from WSQ native library
1.11 10/14/2016- Fix build. Missing armeabi libraries in v1.10
1.10 9/20/2016 - Moved SGNFIQ and SGWSQ object instantiation from SGFPLib constructor to 
                 respective method calls.
1.9 9/9/2016   - Added GetNumOfMinutiae() function
1.8 5/17/2016  - Added support for SecuGen Hamster PRO (HUPx)
1.7 11/3/2015  - Added APIs to NIST WSQ Encoding and Decoding
1.6 9/11/2015  - Added APIs to comput NIST Fingerprint Image Quality
                 Fixed bug preventing Smart Capture from being disabled with U20
1.5 5/15/2015  - Fixed memory leaks on instantiation and initialization 
                 of sgfplib object.
                 Added ReadSerialNumber() and WriteSerialNumber() APIs.
1.4 12/16/2014 - Improved display of fingerprint image in sample app.
                 Fixed thread not terminated issue when home button hit
                 with AUTO ON enabled.   
1.3 5/23/2014  - Added support for AUTO ON
                 Included Intel native libraries  (x86)  
1.2 4/30/2014  - Added support for Hamster PRO 20 (HU20)
1.1 11/6/2013  - Improved image quality with Hamster IV (SDU04P)
                 Improved display of captured images in sample application 
1.0 3/21/2013  - First Release
1.0 Beta2 3/5/2013  - Added InitEx() function
                      Improved SDU04P image quality
1.0 Beta1 1/22/2013 - Initial Release

    