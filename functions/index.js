const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { setGlobalOptions } = require("firebase-functions/v2");
const admin = require("firebase-admin");


admin.initializeApp();


setGlobalOptions({ maxInstances: 10 });


exports.sendPushNotification = onDocumentCreated("users/{userId}/notifications/{notificationId}", async (event) => {

    const snap = event.data;
    if (!snap) {
        console.log("데이터가 없습니다.");
        return null;
    }

    const notificationData = snap.data();
    const receiverId = event.params.userId;

    console.log(`알림 전송 시작: 받는 사람 ID = ${receiverId}`);


    const userDoc = await admin.firestore().collection("users").doc(receiverId).get();

    if (!userDoc.exists) {
        console.log("사용자 문서를 찾을 수 없습니다.");
        return null;
    }

    const userData = userDoc.data();
    const fcmToken = userData.fcmToken;


    if (!fcmToken) {
        console.log("해당 사용자의 FCM 토큰이 없습니다. 알림 전송 취소.");
        return null;
    }


    const payload = {
        notification: {
            title: "Leafy",
            body: notificationData.message || "새로운 알림이 도착했습니다."
        },
        data: {

            type: String(notificationData.type || "SYSTEM"),
            targetPostId: String(notificationData.targetPostId || ""),
            senderId: String(notificationData.senderId || "")
        },
        token: fcmToken
    };


    if (notificationData.type === "LIKE") {
        payload.notification.title = "새로운 좋아요 ";
    } else if (notificationData.type === "COMMENT") {
        payload.notification.title = "새로운 댓글 ";
    } else if (notificationData.type === "FOLLOW") {
        payload.notification.title = "새로운 팔로워 ";
    }


    try {
        const response = await admin.messaging().send(payload);
        console.log("푸시 알림 전송 성공!:", response);
        return response;
    } catch (error) {
        console.error("푸시 알림 전송 실패:", error);
        return null;
    }
});