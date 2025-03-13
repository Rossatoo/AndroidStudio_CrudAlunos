package com.example.exemplocrud1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Aluno.class}, version = 1)
public abstract class appDatabase extends RoomDatabase {
    public abstract AlunoDAO alunoDaoRoom();

    private static appDatabase INSTANCE;

    public static synchronized  appDatabase getInstance(Context context){
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            appDatabase.class,
                            "banco-de-dados"
                    ).allowMainThreadQueries()
                    .build();
        }
            return INSTANCE;
        }
    }

