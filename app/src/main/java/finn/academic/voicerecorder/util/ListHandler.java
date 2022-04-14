package finn.academic.voicerecorder.util;

import java.util.List;

import finn.academic.voicerecorder.model.Folder;

public class ListHandler {
    public static boolean containsName(final List<Folder> list, final String name){
        return list.stream().map(Folder::getName).filter(name::equals).findFirst().isPresent();
    }
}
