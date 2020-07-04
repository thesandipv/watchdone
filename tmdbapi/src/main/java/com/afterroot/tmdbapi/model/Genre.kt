package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.model.core.NamedIdElement
import com.fasterxml.jackson.annotation.JsonRootName

@Deprecated("Use New Genre", replaceWith = ReplaceWith("Genre", "com.afterroot.tmdbapi2.model.Genre"))
@JsonRootName("genre")
class Genre : NamedIdElement() 