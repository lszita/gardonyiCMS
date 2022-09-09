import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './page.reducer';

export const PageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const pageEntity = useAppSelector(state => state.page.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="pageDetailsHeading">
          <Translate contentKey="gardonyiCmsApp.page.detail.title">Page</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{pageEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="gardonyiCmsApp.page.title">Title</Translate>
            </span>
          </dt>
          <dd>{pageEntity.title}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="gardonyiCmsApp.page.content">Content</Translate>
            </span>
          </dt>
          <dd>{pageEntity.content}</dd>
        </dl>
        <Button tag={Link} to="/page" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/page/${pageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PageDetail;
