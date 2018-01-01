/*
 * Copyright 2017, 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ufxcoder.formats;

/**
 * Data class for event which occurred during processing.
 */
public class ProcessorEvent
{
  private final EventSeverity severity;
  private String messageKey;
  private String message;

  public ProcessorEvent(final EventSeverity sev)
  {
    severity = sev;
  }

  public EventSeverity getSeverity()
  {
    return severity;
  }

  public String getMessageKey()
  {
    return messageKey;
  }

  public void setMessageKey(final String messageKey)
  {
    this.messageKey = messageKey;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage(final String message)
  {
    this.message = message;
  }
}
