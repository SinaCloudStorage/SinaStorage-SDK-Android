/*
 * Copyright 2012-2013 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sina.cloudstorage.services.scs.transfer.internal;

import com.sina.cloudstorage.services.scs.transfer.Transfer;
import com.sina.cloudstorage.services.scs.transfer.Transfer.TransferState;

/**
 * Listener for transfer state changes.  Not intended to be consumed externally.
 */
public interface TransferStateChangeListener {
    public void transferStateChanged(Transfer transfer, TransferState state);
}