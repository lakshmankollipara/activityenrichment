package org.lakshman.common.exceptions

class ActivityEnrichmentException(message: String = "Error enriching User Activity", cause: Throwable = None.orNull)
  extends Exception(message, cause)
