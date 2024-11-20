const functions = require("firebase-functions/v1");
const admin = require("firebase-admin");
admin.initializeApp();

exports.notifyUserOnDocumentAdd = functions.firestore
    .document("/Notifications/{userId}/userNotifs/{notifId}")
    .onCreate(async (snapshot, context) => {
            const newNotification = snapshot.data();
            const userId = context.params.userId;

            // extract info from DB
            const userDoc = await admin.firestore().collection("Users").doc(userId).get();
            const notifEnabled = userDoc.get("notificationsEnabled");
            const fcmToken = userDoc.get("fcmToken");

            // If user has notifs disabled, or we can't find their fcm token, abort
            if (!notifEnabled){
                console.log(`Notifications are disabled for user: ${userId}`);
                return null;
            }
            if (!fcmToken) {
                console.log(`No FCM token for user: ${userId}`);
                return null;
            }

            // Create the notification payload
             const message = {
                        token: fcmToken,
                        notification: {
                            title: newNotification.title || "New Notification",
                            body: newNotification.message || "Open PickMe to see it",
                        },
                        android: {
                            priority: "high",
                            notification: {
                                channelId: "pickme_cID",
                                icon: "notification_icon",
                            },
                        },
                    };

            // Send the notification
            try {
                await admin.messaging().send(message);
                console.log(`Notification sent to user: ${userId}`);
            } catch (error) {
                console.error("Error sending notification:", error);
            }

            return null;
        });




