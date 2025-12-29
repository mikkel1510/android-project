const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

async function getUserTokens(uid) {
  const snap = await admin.firestore()
    .collection("users").doc(uid)
    .collection("fcmTokens")
    .get();


  return snap.docs.map(d => d.id);
}

async function getTokensForUsers(uids) {
  const tokens = [];
  for (const uid of uids) {
    const t = await getUserTokens(uid);
    tokens.push(...t);
  }
  return [...new Set(tokens)];
}

async function sendToTokens(tokens, data) {
  if (!tokens.length) return;

  await admin.messaging().sendEachForMulticast({
    tokens,
    android: { priority: "high" },
    data: Object.fromEntries(Object.entries(data).map(([k, v]) => [k, String(v)]))
  });
}

exports.onNewMessage = functions.firestore
  .document("groups/{groupId}/messages/{messageId}")
  .onCreate(async (snap, context) => {
    const { groupId } = context.params;
    const msg = snap.data() || {};

    const senderId = msg.senderId;
    const text = (msg.text || "").slice(0, 120);

    const groupRef = admin.firestore().collection("groups").doc(groupId);
    const groupDoc = await groupRef.get();
    const group = groupDoc.data() || {};

    const memberIDs = group.memberIDs || [];
    const recipients = memberIDs.filter(uid => uid && uid !== senderId);

    const groupName = group.name || "Group";

    let senderName = "Someone";
    if (senderId) {
      const u = await admin.firestore().collection("users").doc(senderId).get();
      senderName = u.data()?.name || u.data()?.displayName || senderName;
    }

    const tokens = await getTokensForUsers(recipients);

    await sendToTokens(tokens, {
      type: "NEW_MESSAGE",
      groupId,
      groupName,
      senderName,
      text
    });
  });

exports.onNewExpense = functions.firestore
  .document("groups/{groupId}/expenses/{expenseId}")
  .onCreate(async (snap, context) => {
    const { groupId } = context.params;
    const exp = snap.data() || {};

    const paidBy = exp.paidBy;
    const description = exp.description || "New expense";

    const groupRef = admin.firestore().collection("groups").doc(groupId);
    const groupDoc = await groupRef.get();
    const group = groupDoc.data() || {};

    const memberIDs = group.memberIDs || [];
    // Usually skip notifying the creator/payer
    const recipients = memberIDs.filter(uid => uid && uid !== paidBy);

    const groupName = group.name || "Group";
    const tokens = await getTokensForUsers(recipients);

    await sendToTokens(tokens, {
      type: "NEW_EXPENSE",
      groupId,
      groupName,
      description
    });
  });

exports.onGroupMemberAdded = functions.firestore
  .document("groups/{groupId}")
  .onUpdate(async (change, context) => {
    const { groupId } = context.params;

    const before = change.before.data() || {};
    const after = change.after.data() || {};

    const beforeMembers = before.memberIDs || [];
    const afterMembers = after.memberIDs || [];

    const added = afterMembers.filter(uid => uid && !beforeMembers.includes(uid));
    if (!added.length) return;

    const groupName = after.name || "a group";
    const tokens = await getTokensForUsers(added);

    await sendToTokens(tokens, {
      type: "ADDED_TO_GROUP",
      groupId,
      groupName
    });
  });
