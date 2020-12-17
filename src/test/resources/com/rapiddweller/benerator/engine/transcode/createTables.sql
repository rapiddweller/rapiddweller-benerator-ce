/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

create table ROLE (
  ID     int         not null,
  NAME   varchar(30) not null,
  constraint ROLE_PK primary key (ID)
);

create table USER (
  ID            int         not null,
  ROLE_FK       int			not null,
  NAME          varchar(30) not null,
  constraint USER_PK primary key (ID),
  constraint ROLE_USER_FK foreign key (ROLE_FK) references ROLE (ID)
);

create table COUNTRY (
  ID     int         not null,
  NAME   varchar(30) not null,
  constraint COUNTRY_PK primary key (ID)
);

create table STATE (
  ID               int         not null,
  COUNTRY_FK       int,
  NAME             varchar(30) not null,
  constraint STATE_PK primary key (ID),
  constraint STATE_COUNTRY_FK foreign key (COUNTRY_FK) references COUNTRY (ID)
);

create table CITY (
  ID       int         not null,
  STATE_FK int,
  NAME     varchar(30) not null,
  constraint CITY_PK primary key (ID),
  constraint CITY_STATE_FK foreign key (STATE_FK) references STATE (ID)
);

