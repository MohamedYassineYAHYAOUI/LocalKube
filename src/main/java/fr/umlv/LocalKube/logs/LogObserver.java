package fr.umlv.LocalKube.logs;

import fr.umlv.LocalKube.app.Application;

/**
 * log observer interface
 */
interface LogObserver {


    default void onCreateApp(Application app, Log log){};

    default void onStart(Log log){};

    default void onStop( int appId){};

    default void onShutDown(){};
}