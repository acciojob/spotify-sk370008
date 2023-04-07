package com.driver;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    //Auto-wire will not work in this case, no need to change this and add autowire

    SpotifyRepository spotifyRepository = new SpotifyRepository();

    public User createUser(String name, String mobile){
        return spotifyRepository.createUser(name,mobile);
    }

    public Artist createArtist(String name) {
        return spotifyRepository.createArtist(name);
    }

    public Album createAlbum(String title, String artistName) {
        return spotifyRepository.createAlbum(title,artistName);
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        return spotifyRepository.createSong(title, albumName, length);
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        return spotifyRepository.createPlaylistOnLength(mobile, title, length);
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        return spotifyRepository.createPlaylistOnName(mobile, title, songTitles);
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        return spotifyRepository.findPlaylist(mobile,playlistTitle);
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        return spotifyRepository.likeSong(mobile,songTitle);
    }

    public String mostPopularArtist() {
        return spotifyRepository.mostPopularArtist();
    }

    public String mostPopularSong() {
        return spotifyRepository.mostPopularSong();
    }

    public List<User> getListOfUsers(){
        return spotifyRepository.getListOfUsers();
    }

    public List<Song> getListOfSongs(){
        return spotifyRepository.getListOfSongs();
    }

    public List<Playlist> getListOfPlaylists(){
        return spotifyRepository.getListOfPlaylists();
    }

    public List<Album> getListOfAlbums(){
        return spotifyRepository.getListOfAlbums();
    }

    public List<Artist> getListOfArtists(){
        return spotifyRepository.getListOfArtists();
    }


    public HashMap<Artist,List<Album>> getartistAlbumMap(){
        return spotifyRepository.getartistAlbumMap();
    }

    public HashMap<Album,List<Song>> getalbumSongMap(){
        return spotifyRepository.getalbumSongMap();
    }

    public HashMap<Playlist,List<Song>> getplaylistSongMap(){
        return spotifyRepository.getplaylistSongMap();
    }

    public HashMap<Playlist,List<User>> getplaylistListenerMap(){
        return spotifyRepository.getplaylistListenerMap();
    }

    public HashMap<User,Playlist> getcreatorPlaylistMap(){
        return spotifyRepository.getcreatorPlaylistMap();
    }

    public HashMap<User,List<Playlist>> getuserPlaylistMap(){
        return spotifyRepository.getuserPlaylistMap();
    }

    public HashMap<Song,List<User>> getsongLikeMap(){
        return spotifyRepository.getsongLikeMap();
    }
}
