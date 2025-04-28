package com.viannele.classicsputsimply.service;

import com.viannele.classicsputsimply.model.Classic;
import java.io.IOException;
import java.util.List;

public interface ClassicsService {
    List<Classic> getAllClassics(String lang) throws IOException;
    Classic getClassicContentBySlug(String slug, String lang) throws IOException;
}