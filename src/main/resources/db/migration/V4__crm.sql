-- CRM: every enquiry (contact form or viewing booking) is a lead in a sales pipeline.

-- Employees belong to a department; leads are auto-assigned within it.
ALTER TABLE agents ADD COLUMN department VARCHAR(20);   -- valuation | planning | sales
UPDATE agents SET department = 'sales';

-- Pipeline stage lives in the existing `status` column:
--   new | in-progress | meeting | contract | won | lost
UPDATE requests SET status = 'won' WHERE status = 'closed';

ALTER TABLE requests ADD COLUMN department  VARCHAR(20);
ALTER TABLE requests ADD COLUMN assignee_id VARCHAR(64) REFERENCES agents(id) ON DELETE SET NULL;
ALTER TABLE requests ADD COLUMN property_id VARCHAR(64);
ALTER TABLE requests ADD COLUMN updated_at  TIMESTAMPTZ;
UPDATE requests SET updated_at = created_at;
ALTER TABLE requests ALTER COLUMN updated_at SET NOT NULL;

CREATE INDEX idx_requests_assignee ON requests (assignee_id);

-- Timeline of a lead: manual notes and automatic system events.
CREATE TABLE crm_notes (
    id          BIGSERIAL     PRIMARY KEY,
    request_id  BIGINT        NOT NULL REFERENCES requests(id) ON DELETE CASCADE,
    kind        VARCHAR(10)   NOT NULL DEFAULT 'note',   -- note | system
    text        TEXT          NOT NULL,
    author      VARCHAR(80)   NOT NULL DEFAULT '',
    created_at  TIMESTAMPTZ   NOT NULL
);
CREATE INDEX idx_crm_notes_request ON crm_notes (request_id, created_at);

-- Follow-up tasks with a deadline; `reminded` marks that the Telegram reminder went out.
CREATE TABLE crm_tasks (
    id           BIGSERIAL     PRIMARY KEY,
    request_id   BIGINT        NOT NULL REFERENCES requests(id) ON DELETE CASCADE,
    title        VARCHAR(200)  NOT NULL,
    due_at       TIMESTAMPTZ   NOT NULL,
    done         BOOLEAN       NOT NULL DEFAULT FALSE,
    reminded     BOOLEAN       NOT NULL DEFAULT FALSE,
    assignee_id  VARCHAR(64)   REFERENCES agents(id) ON DELETE SET NULL,
    created_at   TIMESTAMPTZ   NOT NULL
);
CREATE INDEX idx_crm_tasks_open ON crm_tasks (done, due_at);
CREATE INDEX idx_crm_tasks_request ON crm_tasks (request_id);
