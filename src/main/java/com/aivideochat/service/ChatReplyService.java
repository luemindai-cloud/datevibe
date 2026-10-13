package com.aivideochat.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ChatReplyService {

    private final List<String> replies = List.of(

            "arey yaar kya kar rahe ho 😘",

            "hmm tumse baat karke achha lagta hai ❤️",

            "arey yaar vdo call karo na 🥺",

            "itna busy kyun rehte ho tum 😌",

            "mujhe bhool toh nahi gaye na? 🥺",

            "aaj tumhari bahut yaad aa rahi thi ❤️",

            "achhaaa, phir batao kya hua? 😄",

            "hmm interesting hai yaar 😌",

            "sachii? mujhe bhi aisa hi lagta hai ❤️",

            "hehe tum bhi na 😂",

            "mere saath thoda aur baat karo na 🥰",

            "kya baat hai aaj bade chup chup ho 😌",

            "haan yaar bolo, main sun rahi hoon ❤️",

            "ohh achhaaa 😍",

            "hehe mujhe pata tha tum yehi bologe 😂",

            "really? that's so sweet yaar 🥺❤️",

            "tumhare messages ka wait kar rahi thi 😘",

            "arey waah, ye toh interesting hai 👀",

            "hmm mujhe tumhari baatein pasand hain ❤️",

            "itna cute kyun ho tum 😭❤️",

            "achha batao, aaj ka din kaisa tha? 😊",

            "main yahin hoon, bolo na 🥰",

            "hmm tumse baat karna achha lagta hai 😌❤️",

            "hehe okayyy, phir aage batao 😂",

            "arey yaar tum bhi na 😘"

    );

    public String getRandomReply() {

        int index = ThreadLocalRandom.current()
                .nextInt(replies.size());

        return replies.get(index);
    }
}