package com.afterroot.tmdbapi.model

import com.afterroot.tmdbapi.model.core.NamedIdElement
import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("genre")
class Genre : NamedIdElement() 