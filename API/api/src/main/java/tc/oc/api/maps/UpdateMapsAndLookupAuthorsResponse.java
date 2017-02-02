package tc.oc.api.maps;

import java.util.Map;
import java.util.UUID;

import com.google.common.util.concurrent.ListenableFuture;
import tc.oc.api.docs.virtual.UserDoc;
import tc.oc.api.message.types.UpdateMultiResponse;

public class UpdateMapsAndLookupAuthorsResponse {

    private final ListenableFuture<UpdateMultiResponse> maps;
    private final Map<UUID, ListenableFuture<UserDoc.Identity>> authors;

    public UpdateMapsAndLookupAuthorsResponse(ListenableFuture<UpdateMultiResponse> maps, Map<UUID, ListenableFuture<UserDoc.Identity>> authors) {
        this.maps = maps;
        this.authors = authors;
    }

    public ListenableFuture<UpdateMultiResponse> maps() {
        return maps;
    }

    public Map<UUID, ListenableFuture<UserDoc.Identity>> authors() {
        return authors;
    }
}
