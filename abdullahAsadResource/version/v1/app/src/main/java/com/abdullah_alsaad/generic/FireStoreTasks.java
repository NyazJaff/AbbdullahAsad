package com.abdullah_alsaad.generic;

import com.google.firebase.firestore.FirebaseFirestore;

public class FireStoreTasks {

    public FireStoreTasks() {
    }

    public static void deleteQandA(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("QandA").document(id).delete();
    }

}
