package com.videogamedb.services;

import com.github.javafaker.Faker;
import com.videogamedb.models.*;
import com.videogamedb.repositories.*;
import com.videogamedb.utils.EncryptionUtil;
import com.videogamedb.utils.PasswordService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Translates Python mock data generator to Java.
 * Generates secure mock data with encryption and hashing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataGenerationService {

    private final PlatformRepository platformRepository;
    private final GenreRepository genreRepository;
    private final ContributorRepository contributorRepository;
    private final VideoGameRepository videoGameRepository;
    private final PlatformReleaseRepository platformReleaseRepository;
    private final UserRepository userRepository;
    private final OwnedGameRepository ownedGameRepository;
    private final PlaySessionRepository playSessionRepository;
    private final RatingRepository ratingRepository;
    private final AccessTimeRepository accessTimeRepository;
    private final FollowRepository followRepository;
    private final CollectionRepository collectionRepository;
    private final EncryptionUtil encryptionUtil;
    private final PasswordService passwordService;

    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final String[] ESRB_RATINGS = {"E", "E10+", "T", "M", "A", "RP"};

    @Data
    public static class GeneratedData {
        private List<Platform> platforms;
        private List<Genre> genres;
        private List<Contributor> contributors;
        private List<VideoGame> videoGames;
        private List<PlatformRelease> platformReleases;
        private List<User> users;
        private List<OwnedGame> ownedGames;
        private List<PlaySession> playSessions;
        private List<Rating> ratings;
        private List<AccessTime> accessTimes;
        private List<Follow> follows;
        private List<com.videogamedb.models.Collection> collections;
        private Map<String, String> plainUserPasswords; // For testing
    }

    public GeneratedData generateAllData(int numUsers, int numGames, int numContributors,
                                        int numPlatforms, int numGenres, 
                                        boolean encryptFields, boolean hashPasswords) {
        log.info("🔹 Starting data generation: users={}, games={}, contributors={}, platforms={}, genres={}",
                numUsers, numGames, numContributors, numPlatforms, numGenres);

        GeneratedData data = new GeneratedData();
        data.setPlainUserPasswords(new HashMap<>());

        // Generate reference data
        data.setPlatforms(generatePlatforms(getDefaultPlatformNames(numPlatforms)));
        data.setGenres(generateGenres(getDefaultGenreNames(numGenres)));
        data.setContributors(generateContributors(numContributors));

        // Generate games
        data.setVideoGames(generateVideoGames(numGames, data.getContributors(), data.getGenres()));
        data.setPlatformReleases(generatePlatformReleases(data.getVideoGames(), data.getPlatforms()));

        // Generate users and related data
        UserDataBundle userBundle = generateUsersAndRelatedData(
            numUsers, data.getVideoGames(), data.getPlatforms(), encryptFields, hashPasswords
        );
        data.setUsers(userBundle.getUsers());
        data.setOwnedGames(userBundle.getOwnedGames());
        data.setPlaySessions(userBundle.getPlaySessions());
        data.setRatings(userBundle.getRatings());
        data.setAccessTimes(userBundle.getAccessTimes());
        data.setFollows(userBundle.getFollows());
        data.setPlainUserPasswords(userBundle.getPlainPasswords());

        // Generate collections
        data.setCollections(generateCollections(data.getUsers(), data.getVideoGames()));

        log.info("✅ Data generation complete");
        return data;
    }

    public void saveDataToDatabase(GeneratedData data) {
        log.info("💾 Saving generated data to MongoDB...");

        platformRepository.saveAll(data.getPlatforms());
        genreRepository.saveAll(data.getGenres());
        contributorRepository.saveAll(data.getContributors());
        videoGameRepository.saveAll(data.getVideoGames());
        platformReleaseRepository.saveAll(data.getPlatformReleases());
        userRepository.saveAll(data.getUsers());
        ownedGameRepository.saveAll(data.getOwnedGames());
        playSessionRepository.saveAll(data.getPlaySessions());
        ratingRepository.saveAll(data.getRatings());
        accessTimeRepository.saveAll(data.getAccessTimes());
        followRepository.saveAll(data.getFollows());
        collectionRepository.saveAll(data.getCollections());

        log.info("✅ All data saved to MongoDB");
    }

    private List<Platform> generatePlatforms(List<String> names) {
        List<Platform> platforms = new ArrayList<>();
        for (String name : names) {
            Platform platform = new Platform();
            platform.setId(new ObjectId().toHexString());
            platform.setPlatformName(name);
            platforms.add(platform);
        }
        return platforms;
    }

    private List<Genre> generateGenres(List<String> names) {
        List<Genre> genres = new ArrayList<>();
        for (String name : names) {
            Genre genre = new Genre();
            genre.setId(new ObjectId().toHexString());
            genre.setGenreName(name);
            genres.add(genre);
        }
        return genres;
    }

    private List<Contributor> generateContributors(int count) {
        List<Contributor> contributors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Contributor contributor = new Contributor();
            contributor.setId(new ObjectId().toHexString());
            contributor.setContributorName(faker.company().name());
            contributor.setType(random.nextBoolean() ? "developer" : "publisher");
            contributors.add(contributor);
        }
        return contributors;
    }

    private List<VideoGame> generateVideoGames(int count, List<Contributor> contributors, List<Genre> genres) {
        List<VideoGame> games = new ArrayList<>();
        List<String> devIds = contributors.stream()
            .filter(c -> "developer".equals(c.getType()))
            .map(Contributor::getId)
            .toList();
        List<String> pubIds = contributors.stream()
            .filter(c -> "publisher".equals(c.getType()))
            .map(Contributor::getId)
            .toList();
        List<String> genreIds = genres.stream().map(Genre::getId).toList();

        for (int i = 0; i < count; i++) {
            VideoGame game = new VideoGame();
            game.setId(new ObjectId().toHexString());
            game.setTitle(faker.commerce().productName().replace(".", ""));
            game.setEsrb(ESRB_RATINGS[random.nextInt(ESRB_RATINGS.length)]);

            // Random developers and publishers
            int numDevs = devIds.isEmpty() ? 0 : random.nextInt(Math.min(2, devIds.size())) + 1;
            int numPubs = pubIds.isEmpty() ? 0 : random.nextInt(Math.min(2, pubIds.size())) + 1;
            int numGenres = random.nextInt(Math.min(3, genreIds.size())) + 1;

            game.setDevelopers(randomSample(devIds, numDevs));
            game.setPublishers(randomSample(pubIds, numPubs));
            game.setGenres(randomSample(genreIds, numGenres));

            games.add(game);
        }
        return games;
    }

    private List<PlatformRelease> generatePlatformReleases(List<VideoGame> games, List<Platform> platforms) {
        List<PlatformRelease> releases = new ArrayList<>();

        for (VideoGame game : games) {
            int numPlatforms = random.nextInt(Math.min(3, platforms.size())) + 1;
            List<Platform> selectedPlatforms = randomSample(platforms, numPlatforms);

            for (Platform platform : selectedPlatforms) {
                PlatformRelease release = new PlatformRelease();
                release.setId(new ObjectId().toHexString());
                release.setGameId(game.getId());
                release.setPlatformId(platform.getId());
                release.setPrice(Math.round(random.nextDouble() * 75.0 + 4.99) / 100.0 * 100.0);
                release.setReleaseDate(randomPastDate(3650)); // Within 10 years
                releases.add(release);
            }
        }
        return releases;
    }

    @Data
    private static class UserDataBundle {
        private List<User> users;
        private List<OwnedGame> ownedGames;
        private List<PlaySession> playSessions;
        private List<Rating> ratings;
        private List<AccessTime> accessTimes;
        private List<Follow> follows;
        private Map<String, String> plainPasswords;
    }

    private UserDataBundle generateUsersAndRelatedData(int count, List<VideoGame> games, 
                                                       List<Platform> platforms, 
                                                       boolean encryptFields, boolean hashPasswords) {
        UserDataBundle bundle = new UserDataBundle();
        bundle.setUsers(new ArrayList<>());
        bundle.setOwnedGames(new ArrayList<>());
        bundle.setPlaySessions(new ArrayList<>());
        bundle.setRatings(new ArrayList<>());
        bundle.setAccessTimes(new ArrayList<>());
        bundle.setFollows(new ArrayList<>());
        bundle.setPlainPasswords(new HashMap<>());

        for (int i = 0; i < count; i++) {
            String usernamePlain = faker.name().username();
            String emailPlain = faker.internet().emailAddress();
            String firstNamePlain = faker.name().firstName();
            String lastNamePlain = faker.name().lastName();
            String passwordPlain = faker.internet().password(12, 20);

            String userId = new ObjectId().toHexString();

            // Store plain password for testing
            bundle.getPlainPasswords().put(usernamePlain, passwordPlain);

            // Hash password
            PasswordService.HashedPassword hashedPw = passwordService.hashPassword(passwordPlain);

            // Create user
            User user = new User();
            user.setId(userId);
            user.setUsernameEnc(encryptFields ? encryptionUtil.encryptField(usernamePlain) : usernamePlain);
            user.setUsernameHash(encryptionUtil.hashUsername(usernamePlain));
            user.setEmailEnc(encryptFields ? encryptionUtil.encryptField(emailPlain) : emailPlain);
            user.setEmailMasked(encryptionUtil.maskEmail(emailPlain));
            user.setFirstNameEnc(encryptFields ? encryptionUtil.encryptField(firstNamePlain) : firstNamePlain);
            user.setLastNameEnc(encryptFields ? encryptionUtil.encryptField(lastNamePlain) : lastNamePlain);
            user.setPasswordHash(hashedPw.getHashBase64());
            user.setPasswordSalt(hashedPw.getSaltBase64());
            user.setPasswordIterations(hashedPw.getIterations());
            user.setCreationDate(randomPastDate(1825)); // Within 5 years
            user.setRole("USER");
            user.setPlatforms(randomSample(platforms, random.nextInt(Math.min(2, platforms.size())) + 1)
                .stream().map(Platform::getId).toList());
            user.setAuditToken(encryptionUtil.generateSecureToken(16));
            user.setFailedLoginAttempts(0);
            user.setMaxLoginAttempts(5);
            user.setAccountLockMinutes(30);

            bundle.getUsers().add(user);

            // Generate owned games
            int ownedCount = random.nextInt(Math.min(5, games.size())) + 1;
            List<VideoGame> ownedGames = randomSample(games, ownedCount);

            for (VideoGame game : ownedGames) {
                OwnedGame owned = new OwnedGame();
                owned.setId(new ObjectId().toHexString());
                owned.setUserId(userId);
                owned.setGameId(game.getId());
                owned.setAcquisitionDate(randomPastDate(1095)); // Within 3 years
                bundle.getOwnedGames().add(owned);

                // Play sessions (1-3 per owned game)
                int playSessionCount = random.nextInt(3) + 1;
                for (int j = 0; j < playSessionCount; j++) {
                    int baseHours = random.nextInt(50) + 1;
                    // Add Gaussian noise for privacy
                    int noisyHours = Math.max(1, baseHours + (int) (random.nextGaussian() * 2));

                    PlaySession playSession = new PlaySession();
                    playSession.setId(new ObjectId().toHexString());
                    playSession.setUserId(userId);
                    playSession.setGameId(game.getId());
                    playSession.setDatetimeOpened(randomPastDate(730)); // Within 2 years
                    playSession.setTimePlayed(noisyHours * 3600); // Convert to seconds
                    bundle.getPlaySessions().add(playSession);
                }

                // Ratings (80% chance)
                if (random.nextDouble() < 0.8) {
                    Rating rating = new Rating();
                    rating.setId(new ObjectId().toHexString());
                    rating.setUserId(userId);
                    rating.setGameId(game.getId());
                    rating.setRating(random.nextInt(5) + 1);
                    rating.setRatingDate(randomPastDate(365)); // Within 1 year
                    bundle.getRatings().add(rating);
                }
            }

            // Access times (5-20 per user)
            int accessCount = random.nextInt(16) + 5;
            for (int j = 0; j < accessCount; j++) {
                AccessTime accessTime = new AccessTime();
                accessTime.setId(new ObjectId().toHexString());
                accessTime.setUserId(userId);
                accessTime.setTime(randomPastDate(730)); // Within 2 years
                bundle.getAccessTimes().add(accessTime);
            }
        }

        // Generate follows (after all users created)
        List<String> userIds = bundle.getUsers().stream().map(User::getId).toList();
        for (User user : bundle.getUsers()) {
            int followCount = random.nextInt(Math.min(10, userIds.size() - 1));
            List<String> candidates = userIds.stream()
                .filter(id -> !id.equals(user.getId()))
                .toList();

            List<String> followedIds = randomSample(candidates, followCount);
            for (String followedId : followedIds) {
                Follow follow = new Follow();
                follow.setId(new ObjectId().toHexString());
                follow.setFollowerId(user.getId());
                follow.setFollowedId(followedId);
                bundle.getFollows().add(follow);
            }
        }

        return bundle;
    }

    /**
     * Generates mock collections for users.
     * Each user may have 0-2 collections, each containing 1-5 games.
     *
     * @param users List of generated users
     * @param games List of generated video games
     * @return List of generated collections
     */
    private List<com.videogamedb.models.Collection> generateCollections(List<User> users, List<VideoGame> games) {
        List<com.videogamedb.models.Collection> collections = new ArrayList<>();
        List<String> gameIds = games.stream().map(VideoGame::getId).toList();

        for (User user : users) {
            int collectionCount = random.nextInt(3); // 0-2 collections per user

            for (int i = 0; i < collectionCount; i++) {
                com.videogamedb.models.Collection collection = new com.videogamedb.models.Collection();
                collection.setId(new ObjectId().toHexString());
                collection.setName(faker.lorem().word() + " Collection");
                collection.setDescription(faker.lorem().sentence());
                collection.setUserId(user.getId());

                int gamesInCollection = gameIds.isEmpty() ? 0 : 
                    random.nextInt(Math.min(5, gameIds.size())) + 1;
                collection.setGames(randomSample(gameIds, gamesInCollection));

                collections.add(collection);
            }
        }

        return collections;
    }

    // Helper methods
    private <T> List<T> randomSample(List<T> list, int count) {
        if (list.isEmpty() || count <= 0) {
            return List.of();
        }
        count = Math.min(count, list.size());
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, count);
    }

    private LocalDateTime randomPastDate(int maxDaysAgo) {
        long minDay = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(maxDaysAgo);
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, System.currentTimeMillis());
        return LocalDateTime.ofInstant(
            new Date(randomDay).toInstant(),
            ZoneId.systemDefault()
        );
    }

    private List<String> getDefaultPlatformNames(int count) {
        List<String> all = List.of("PC", "PlayStation 5", "Xbox Series X", "Nintendo Switch", 
                                   "PlayStation 4", "Xbox One", "Nintendo 3DS", "Steam Deck");
        return all.subList(0, Math.min(count, all.size()));
    }

    private List<String> getDefaultGenreNames(int count) {
        List<String> all = List.of("Action", "Adventure", "RPG", "Strategy", "Simulation", 
                                   "Sports", "Puzzle", "Horror", "Racing", "Fighting", "Platformer");
        return all.subList(0, Math.min(count, all.size()));
    }
}
