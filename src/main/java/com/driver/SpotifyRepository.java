package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {

    //contains artist and list of albums
    public HashMap<Artist, List<Album>> artistAlbumMap;
    //contains album and list of songs
    public HashMap<Album, List<Song>> albumSongMap;
    //contains playlist and list of songs with same length and with same name
    public HashMap<Playlist, List<Song>> playlistSongMap;
    //contains playlist and list of listeners(users) of that playlist
    public HashMap<Playlist, List<User>> playlistListenerMap;
    //contains creator of playlist and the playlist
    public HashMap<User, Playlist> creatorPlaylistMap;
    //contains user and  his list of playlists
    public HashMap<User, List<Playlist>> userPlaylistMap;
    //contains song and the list of users that liked the song
    public HashMap<Song, List<User>> songLikeMap;




    //contains list of users
    public List<User> users;
    //contains list of songs
    public List<Song> songs;
    //contains list of playlists
    public List<Playlist> playlists;
    //contains list of albums
    public List<Album> albums;
    //contains list of artists
    public HashSet<Artist> artists;


    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new HashSet<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        userPlaylistMap.put(user,new ArrayList<>());

        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        if (artists.contains(artist)) {
            artistAlbumMap.put(artist, new ArrayList<>());
            artists.add(artist);
        }
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        //If the artist does not exist, first create an artist with given name
        //Create an album with given title and artist


        Artist artist = null;
        boolean doesArtistExist = false;
        for (Artist artistInList : artists){
            if(artistInList.getName().equals(artistName)){
                doesArtistExist = true;
                artist = artistInList;
            }
        }

        if (!doesArtistExist && !artistAlbumMap.containsKey(artist)){
            artist = createArtist(artistName);
        }

        Album album = new Album(title);
        albums.add(album);
        if (artistAlbumMap.containsKey(artist)) {
            artistAlbumMap.get(artist).add(album);
        }


        //last change
        if (albumSongMap.containsKey(album)) {
            albumSongMap.put(album, new ArrayList<>());
        }
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        //If the album does not exist in database, throw "Album does not exist" exception
        //Create and add the song to respective album
        boolean doesAlbumExist = false;
        Album album = null;
        for (Album album1:albums){
            if (album1.getTitle().equals(albumName)){
                doesAlbumExist = true;
                album = album1;
            }
        }
        if (!doesAlbumExist){
            throw new Exception("Album does not exist");
        }
        Song song = new Song(title,length);
        songs.add(song);
        albumSongMap.get(album).add(song);
        songLikeMap.put(song,new ArrayList<>());
        return song;
    }

    public Playlist createPlaylistOnLength(/*creator of playlist*/String mobile,
            /*playlist title*/ String title,/*length of songs to be added*/ int length) throws Exception {
        //Create a playlist with given title and add all songs having the given length in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
        boolean doesUserExist = false;
        User creatorOfPlaylist = null;
        for (User user :users){
            if (user.getMobile().equals(mobile)){
                doesUserExist = true;
                creatorOfPlaylist = user;
            }
        }
        if (!doesUserExist){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        List<Song> songWithSameLength = new ArrayList<>();
        for (Song song :songs){
            if (song.getLength() == length){
                songWithSameLength.add(song);
            }
        }
        playlistSongMap.put(playlist,songWithSameLength);

        List<User> userList = new ArrayList<>();

        playlistListenerMap.put(playlist,userList);

        userList.add(creatorOfPlaylist);
        creatorPlaylistMap.put(creatorOfPlaylist,playlist);

        List<Playlist> playlists1 = userPlaylistMap.get(creatorOfPlaylist);
        playlists1.add(playlist);
        userPlaylistMap.put(creatorOfPlaylist,playlists1);

        return playlist;
    }


    public Playlist createPlaylistOnName(/**/String mobile, /*playlist title*/String title,
            /*list of songs to be added*/ List<String> songTitles) throws Exception {
        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creator of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
        boolean doesUserExist = false;
        User creatorOfPlaylist = null;
        for (User user :users){
            if (user.getMobile().equals(mobile)){
                doesUserExist = true;
                creatorOfPlaylist = user;
            }
        }
        if (!doesUserExist){
            throw new Exception("User does not exist");
        }

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> songWithSameTitle = new ArrayList<>();
        for (String songName :songTitles){
            for (Song song : songs){
                if(song.getTitle().equals(songName)){
                        songWithSameTitle.add(song);
                }
            }
        }
//        for (Song song : songs) {
//            if (song.getTitle().equals(title)) {
//                songWithSameTitle.add(song);
//            }
//        }


        playlistSongMap.put(playlist, new ArrayList<>(songWithSameTitle));

        List<User> userList = new ArrayList<>();
        userList.add(creatorOfPlaylist);
        playlistListenerMap.put(playlist, userList);


        creatorPlaylistMap.put(creatorOfPlaylist, playlist);




        List<Playlist> playlists1 = userPlaylistMap.get(creatorOfPlaylist);
        playlists1.add(playlist);
        userPlaylistMap.put(creatorOfPlaylist, playlists1);
        return playlist;
    }


    public Playlist findPlaylist(/*user to be added*/String mobile,/*playlist to be found*/ String playlistTitle) throws Exception {
        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creator or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exists, throw "Playlist does not exist" exception
        // Return the playlist after updating

        boolean doesUserExists = false;
        User user = null;
        for (User user1 : users){
            if (user1.getMobile().equals(mobile)){
                doesUserExists = true;
                user = user1;
            }
        }

        boolean doesPlaylistExists = false;
        Playlist playlist = null;
        for (Playlist playlist1 : playlists){
            if (playlist1.getTitle().equals(playlistTitle)){
                doesPlaylistExists = true;
                playlist = playlist1;
            }
        }


        boolean isUserCreator = false;

        boolean isUserListener = false;


        if (creatorPlaylistMap.containsKey(user)){
            isUserCreator = true;
            isUserListener = true;
        }

        if (playlistListenerMap.containsKey(playlist)){
            for (User user1 : playlistListenerMap.get(playlist)){
                if (user1.equals(user)){
                    isUserListener = true;
                }
            }
        }

        if (playlistListenerMap.containsKey(playlist)) {
            List<User> playlistUsers = playlistListenerMap.get(playlist);
            playlistUsers.add(user);
            playlistListenerMap.put(playlist, playlistUsers);
        }

        if (userPlaylistMap.containsKey(user)) {
            List<Playlist> playlistList = userPlaylistMap.get(user);
            playlistList.add(playlist);
            userPlaylistMap.put(user, playlistList);
        }
        return playlist;
    }

    //Errors = 2
    public Song likeSong(/*user who liked the song*/String mobile, /*song that was liked*/String songTitle) throws Exception {
        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating


        boolean doesUserExists = false;
        User user = null;
        for (User user1 : users){
            if (user1.getMobile().equals(mobile)){
                doesUserExists = true;
                user = user1;
            }
        }

        if (!doesUserExists){
            throw  new Exception("User does not exist");
        }


        boolean doesSongExists = false;
        Song song = null;
        for (Song song1 : songs){
            if (song1.getTitle().equals(songTitle)){
                doesSongExists = true;
                song = song1;
            }
        }

        if (!doesSongExists){
            throw new Exception("Song does not exist");
        }

        boolean userAlreadyLikedTheSong = false;
        List<User> usersThatAlreadyLikedTheSong = null;
        if (songLikeMap.containsKey(song)) {
            usersThatAlreadyLikedTheSong = songLikeMap.get(song);
        }
        for (User user1 : usersThatAlreadyLikedTheSong){
            if (user.getMobile().equals(mobile)){
                userAlreadyLikedTheSong = true;
            }
        }

        if (!userAlreadyLikedTheSong){
            //increase song like count
            song.setLikes(song.getLikes()+1);
            //add user to the list of user who liked the song
            List<User> userList  = songLikeMap.get(song);
            userList.add(user);
            if (songLikeMap.containsKey(song)) {
                songLikeMap.put(song, userList);
            }
            //increase artist like count
//            for (Artist artist : artistAlbumMap.keySet()){
//                List<Album> albumList = artistAlbumMap.get(artist);
//                for (Album album : albumList){
//                    List<Song> songList = albumSongMap.get(album);
//                    for (Song song1 : songList){
//                        if (song1.getTitle().equals(songTitle)){
//                            artist.setLikes(artist.getLikes()+1);
//                            return song;
//                        }
//                    }
//                }
//            }

            //To increase artist count,we need to find album of that artist and then the artist itself
            Album albumToWhichSongBelongs = null;
            for (Album album : albumSongMap.keySet()){
                for (Song song1 : albumSongMap.get(album)){
                    if (song1.getTitle().equals(songTitle)){
                        albumToWhichSongBelongs = album;
                        break;
                    }
                }
            }

            Artist artistToWhomSongBelongs = null;
            for (Artist artist : artistAlbumMap.keySet()){
                for (Album album : artistAlbumMap.get(artist)){
                    if (albumToWhichSongBelongs.equals(album)){
                        artistToWhomSongBelongs = artist;
                        break;
                    }
                }
            }
            artistToWhomSongBelongs.setLikes(artistToWhomSongBelongs.getLikes()+1);
        }




        return song;
    }

    public Artist getArtistUsingSongTitle(String songTitle){
//        List<Album> albumList = null;
//        List<Song> songList = null;
//        Artist belongsToArtist = null;
//        for (Artist artist : artistAlbumMap.keySet()){
//            albumList = artistAlbumMap.get(artist);
//            for (Album album : albumList){
//                songList = albumSongMap.get(album);
//                for (Song song :songList){
//                    if (song.getTitle().equals(songTitle)){
//                        belongsToArtist = artist;
//                    }
//                }
//            }
//        }

        Artist artistToBeFound = null;
        for (Artist artist : artistAlbumMap.keySet()){
            List<Album> albumList = artistAlbumMap.get(artist);
            for (Album album : albumList){
                List<Song> songList = albumSongMap.get(album);
                for (Song song1 : songList){
                    if (song1.getTitle().equals(songTitle)){
                        artistToBeFound = artist;
                    }
                }
            }
        }



        return artistToBeFound;
    }
    public String mostPopularArtist() {
        String mostPopular = null;
        int likes = Integer.MIN_VALUE;
        for (Artist artist :artists.){
            if (artist.getLikes()>likes){
                likes = artist.getLikes();
                mostPopular = artist.getName();
            }
        }
        return mostPopular;
    }

    public String mostPopularSong() {
        String mostPopular = null;
        int likes = Integer.MIN_VALUE;
        for (Song song : songs){
            if (song.getLikes()>likes){
                likes = song.getLikes();
                mostPopular = song.getTitle();
            }
        }
        return mostPopular;
    }















    public List<User> getListOfUsers(){
        return users;
    }

    public List<Song> getListOfSongs(){
        return songs;
    }

    public List<Playlist> getListOfPlaylists(){
        return playlists;
    }

    public List<Album> getListOfAlbums(){
        return albums;
    }

    public HashSet<Artist> getListOfArtists(){
        return artists;
    }

    public HashMap<Artist,List<Album>> getartistAlbumMap(){
        return artistAlbumMap;
    }

    public HashMap<Album,List<Song>> getalbumSongMap(){
        return albumSongMap;
    }

    public HashMap<Playlist,List<Song>> getplaylistSongMap(){
        return playlistSongMap;
    }

    public HashMap<Playlist,List<User>> getplaylistListenerMap(){
        return playlistListenerMap;
    }

    public HashMap<User,Playlist> getcreatorPlaylistMap(){
        return creatorPlaylistMap;
    }

    public HashMap<User,List<Playlist>> getuserPlaylistMap(){
        return userPlaylistMap;
    }

    public HashMap<Song,List<User>> getsongLikeMap(){
        return songLikeMap;
    }


}
