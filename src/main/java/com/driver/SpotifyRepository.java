package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    //contains artist and list of albums
    public HashMap<Artist, List<Album>> artistAlbumMap;
    //contains album and list of songs
    public HashMap<Album, List<Song>> albumSongMap;
    //contains playlist and list of songs with same length
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
    public List<Artist> artists;

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
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
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
        Album album = new Album(title);
        albums.add(album);
        if (!doesArtistExist){
            artist = new Artist(artistName);
            artistAlbumMap.put(artist,albums);
            return album;
        }else {
            artistAlbumMap.put(artist,albums);
            return album;
        }

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
        albumSongMap.put(album,songs);
        songLikeMap.put(song,new ArrayList<>());
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
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
        userList.add(creatorOfPlaylist);
        creatorPlaylistMap.put(creatorOfPlaylist,playlist);
        playlistListenerMap.put(playlist,userList);
        List<Playlist> playlists1 = new ArrayList<>();
        playlists1.add(playlist);
        userPlaylistMap.put(creatorOfPlaylist,playlists1);
        return playlist;
    }


    //Error => NullPointer Exception      1
    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
        boolean doesUserExist = false;
        User creatorOfPlaylist = null;
        Playlist playlist = null;
        for (User user :users){
            if (user.getMobile().equals(mobile)){
                doesUserExist = true;
                creatorOfPlaylist = user;
            }
        }
        if (!doesUserExist){
            throw new Exception("User does not exist");
        }

        playlist = new Playlist(title);
        playlists.add(playlist);
        List<Song> songWithSameTitle = new ArrayList<>();
        for (String songName :songTitles){
            for (Song song : songs){
                if(song.getTitle().equals(songName)){
                        songWithSameTitle.add(song);
                }
            }
        }
        for (Song song : songs) {
            if (song.getTitle().equals(title)) {
                songWithSameTitle.add(song);
            }
        }

        playlistSongMap.put(playlist, songWithSameTitle);
        creatorPlaylistMap.put(creatorOfPlaylist, playlist);
        List<User> userList = new ArrayList<>();
        userList.add(creatorOfPlaylist);
        playlistListenerMap.put(playlist, userList);
        List<Playlist> playlists1 = new ArrayList<>();
        playlists1.add(playlist);
        userPlaylistMap.put(creatorOfPlaylist, playlists1);

        return playlist;
    }


    //Error => NullPointer Exception     3
    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creater or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exists, throw "Playlist does not exist" exception
        // Return the playlist after updating
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

        boolean doesPlaylistExist = false;
        for (Playlist playlist:playlists){
            if (playlist.getTitle().equals(playlistTitle)){
                doesPlaylistExist = true;
            }
        }
        if (!doesPlaylistExist){
            throw new Exception("Playlist does not exist");
        }

        boolean isUserCreatorOrListener = false;
        for (User user : creatorPlaylistMap.keySet()){
            if (user.getMobile().equals(mobile)){
                isUserCreatorOrListener = true;
            }
        }

//        playlistListenerMap.get()
        Playlist playlistWithGivenTitle = null;
        List<User> listenerUsers = new ArrayList<>();
        for (Playlist playlist : playlistListenerMap.keySet()){
            if (playlist.getTitle().equals(playlistTitle)){
                playlistWithGivenTitle = playlist;
                listenerUsers = playlistListenerMap.get(playlist);
            }
        }
        for (User user: listenerUsers){
            if (user.getMobile().equals(mobile)){
                isUserCreatorOrListener = true;
            }
        }
        for (User user :users){
            if (user.getMobile().equals(mobile)){
                listenerUsers.add(user);
            }
        }
//        listenerUsers.add();
        if (!isUserCreatorOrListener){
            playlistListenerMap.put(playlistWithGivenTitle,listenerUsers);
        }
        return playlistWithGivenTitle;
    }

    //Errors = 2
    public Song likeSong(String mobile, String songTitle) throws Exception {
        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating
        boolean doesSongExist = false;
        boolean doesUserExist = false;
        Song songThatWasLiked = null;
        User userThatLikedTheSong = null;
        for (Song song :songs){
            if (song.getTitle().equals(songTitle)){
                doesSongExist = true;
                songThatWasLiked = song;
            }
        }
        for (User user : users){
            if (user.getMobile().equals(mobile)){
                doesUserExist = true;
                userThatLikedTheSong = user;
            }
        }

        List<User> allUsersThatLikedTheSong = new ArrayList<>();
        allUsersThatLikedTheSong = songLikeMap.get(songThatWasLiked);
        boolean alreadyLiked = false;
        for (User user:allUsersThatLikedTheSong){
            if (user.getMobile().equals(mobile)){
                alreadyLiked = true;
            }
        }

        Artist artist = getArtistUsingSongTitle(songTitle);

        if ((doesSongExist && doesUserExist) && !(alreadyLiked)){
            allUsersThatLikedTheSong.add(userThatLikedTheSong);
            songLikeMap.put(songThatWasLiked,allUsersThatLikedTheSong);
            songThatWasLiked.setLikes(songThatWasLiked.getLikes() + 1);
            artist.setLikes(artist.getLikes() + 1);
        }
        return songThatWasLiked;
    }

    public Artist getArtistUsingSongTitle(String songTitle){
        List<Album> albumList = null;
        List<Song> songList = null;
        Artist belongsToArtist = null;
        for (Artist artist : artistAlbumMap.keySet()){
            albumList = artistAlbumMap.get(artist);
            for (Album album : albumList){
                songList = albumSongMap.get(album);
                for (Song song :songList){
                    if (song.getTitle().equals(songTitle)){
                        belongsToArtist = artist;
                    }
                }
            }
        }
        return belongsToArtist;
    }
    public String mostPopularArtist() {
        String mostPopular = null;
        int likes = Integer.MIN_VALUE;
        for (Artist artist :artists){
            if (artist.getLikes()>=likes){
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
}
