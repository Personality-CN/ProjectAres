package tc.oc.api.minecraft.users;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import tc.oc.api.docs.Punishment;
import tc.oc.api.docs.Session;
import tc.oc.api.docs.UserId;
import tc.oc.api.docs.Whisper;
import tc.oc.api.docs.virtual.UserDoc;
import tc.oc.api.exceptions.NotFound;
import tc.oc.api.minecraft.sessions.LocalSessionFactory;
import tc.oc.api.model.NullModelService;
import tc.oc.api.users.ChangeClassRequest;
import tc.oc.api.users.ChangeSettingRequest;
import tc.oc.api.users.CreditRaindropsRequest;
import tc.oc.api.users.LoginRequest;
import tc.oc.api.users.LoginResponse;
import tc.oc.api.users.LogoutRequest;
import tc.oc.api.users.PurchaseGizmoRequest;
import tc.oc.api.users.UserSearchRequest;
import tc.oc.api.users.UserSearchResponse;
import tc.oc.api.users.UserService;
import tc.oc.api.users.UserUpdateResponse;
import tc.oc.api.util.UUIDs;
import tc.oc.commons.core.concurrent.FutureUtils;
import tc.oc.minecraft.api.server.LocalServer;
import tc.oc.minecraft.api.user.User;
import tc.oc.minecraft.api.user.UserFactory;
import tc.oc.minecraft.api.user.UserFinder;

@Singleton
public class LocalUserService extends NullModelService<tc.oc.api.docs.User, UserDoc.Partial> implements UserService {

    @Inject private LocalServer minecraftServer;
    @Inject private LocalSessionFactory sessionFactory;
    @Inject private UserFinder userFinder;
    @Inject private UserFactory userFactory;

    @Override
    public ListenableFuture<tc.oc.api.docs.User> find(UserId userId) {
        return FutureUtils.mapSync(
            userFinder.findUserAsync(UUIDs.parse(userId.player_id())),
            LocalUserDocument::new
        );
    }

    @Override
    public ListenableFuture<UserSearchResponse> search(UserSearchRequest request) {
        for(User player : minecraftServer.getSavedPlayers()) {
            if(player.lastKnownName()
                     .filter(name -> name.equalsIgnoreCase(request.username))
                     .isPresent()) {
                return Futures.immediateFuture(new UserSearchResponse(new LocalUserDocument(player),
                                                                      player.isOnline(),
                                                                      false,
                                                                      null,
                                                                      null));
            }
        }
        return Futures.immediateFailedFuture(new NotFound("No user named '" + request.username + "'", null));
    }

    @Override
    public ListenableFuture<LoginResponse> login(LoginRequest request) {
        final tc.oc.api.docs.User user = new LocalUserDocument(userFactory.createUser(request.uuid, request.username, Instant.now()));
        final Session session = request.start_session ? sessionFactory.newSession(user, request.ip)
                                                      : null;

        return Futures.immediateFuture(new LoginResponse() {
            @Override
            public @Nullable String kick() {
                return null;
            }

            @Override
            public @Nullable String message() {
                return null;
            }

            @Override
            public @Nullable String route_to_server() {
                return null;
            }

            @Override
            public tc.oc.api.docs.User user() {
                return user;
            }

            @Override
            public @Nullable Session session() {
                return session;
            }

            @Override
            public @Nullable Punishment punishment() {
                return null;
            }

            @Override
            public List<Whisper> whispers() {
                return Collections.emptyList();
            }

            @Override
            public int unread_appeal_count() {
                return 0;
            }
        });
    }

    @Override
    public ListenableFuture<?> logout(LogoutRequest request) {
        return Futures.immediateFuture(null);
    }

    @Override
    public ListenableFuture<UserUpdateResponse> creditRaindrops(UserId userId, CreditRaindropsRequest request) {
        return FutureUtils.mapSync(find(userId), user -> new UserUpdateResponse() {
            @Override
            public boolean success() {
                return true;
            }

            @Override
            public tc.oc.api.docs.User user() {
                return user;
            }
        });
    }

    @Override
    public ListenableFuture<tc.oc.api.docs.User> purchaseGizmo(UserId userId, PurchaseGizmoRequest request) {
        return find(userId);
    }

    @Override
    public <T extends UserDoc.Partial> ListenableFuture<tc.oc.api.docs.User> update(UserId userId, T update) {
        return find(userId);
    }

    @Override
    public ListenableFuture<tc.oc.api.docs.User> changeSetting(UserId userId, ChangeSettingRequest request) {
        return find(userId);
    }

    @Override
    public ListenableFuture<tc.oc.api.docs.User> changeClass(UserId userId, ChangeClassRequest request) {
        return find(userId);
    }
}
