// Use DBML to define your database structure
// Docs: https://dbml.dbdiagram.io/docs

Table Artist {
  id integer [primary key, increment]
  metallum_id string
  country string [not null]
  name string [not null]
  url string
  image_url string
}

enum ReleaseType {
  "Full-length"
  "Demo"
  "EP"
  "Compilation"
  "Single"
  "Split"
  "Boxed set"
  "Collaboration"
  "Live album"
  "Video"
  "Split video"
}

Table Release {
  id integer [primary key, increment]
  metallum_id string
  name string [not null]
  category string [not null]
  year integer [not null]
  release_type ReleaseType [not null]
  trivia string
  createdAt integer [not null, note: "epoch milliseconds"]
  url string
  image_url string
  artist_id integer
}

Ref: Release.artist_id > Artist.id

Table Song {
  id integer [primary key, increment]
  metallum_id string
  name string [not null]
  length string [not null]
  release_id integer [not null]
}

Ref: Song.release_id > Release.id

enum LyricsType {
  "Lyrics available"
  "Lyrics not available"
  "Instrumental"
}

Table Lyrics {
  id integer [primary key, increment]
  lyrics_type LyricsType [not null]
  lyrics string [not null]
  song_id integer [not null]
}

Ref: Lyrics.song_id - Song.id

Table Recommendation {
  id integer [primary key, increment]
  score integer [not null]
  artist_id integer [not null]
  artist_recommended_id integer [not null]
}

Ref: Recommendation.artist_id > Artist.id
Ref: Recommendation.artist_recommended_id > Artist.id

Table Lineup {
  id integer [primary key, increment]
  name string [not null]
  role string [not null]
  release_id integer [not null]
}

Ref: Lineup.release_id > Release.id

Table Video {
  id integer [primary key, increment]
  youtube_id string [not null]
  release_id integer [not null]
}

Ref: Video.release_id - Release.id

